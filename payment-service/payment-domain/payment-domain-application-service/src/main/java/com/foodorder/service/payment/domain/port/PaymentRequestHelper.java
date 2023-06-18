package com.foodorder.service.payment.domain.port;

import com.foodorder.domain.valueobject.CustomerId;
import com.foodorder.service.payment.domain.PaymentDomainService;
import com.foodorder.service.payment.domain.dto.PaymentRequestDTO;
import com.foodorder.service.payment.domain.entity.CreditEntry;
import com.foodorder.service.payment.domain.entity.CreditHistory;
import com.foodorder.service.payment.domain.entity.Payment;
import com.foodorder.service.payment.domain.event.PaymentEvent;
import com.foodorder.service.payment.domain.exception.PaymentDomainApplicationServiceException;
import com.foodorder.service.payment.domain.mapper.PaymentDataMapper;
import com.foodorder.service.payment.domain.port.output.message.publisher.PaymentCancelledMsgPublisher;
import com.foodorder.service.payment.domain.port.output.message.publisher.PaymentCompletedMsgPublisher;
import com.foodorder.service.payment.domain.port.output.message.publisher.PaymentFailedMsgPublisher;
import com.foodorder.service.payment.domain.port.output.repository.CreditEntryRepository;
import com.foodorder.service.payment.domain.port.output.repository.CreditHistoryRepository;
import com.foodorder.service.payment.domain.port.output.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRequestHelper {

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final PaymentCompletedMsgPublisher paymentCompletedMsgPublisher;
    private final PaymentCancelledMsgPublisher paymentCancelledMsgPublisher;
    private final PaymentFailedMsgPublisher paymentFailedMsgPublisher;

    public PaymentRequestHelper(PaymentDomainService paymentDomainService,
                                PaymentDataMapper paymentDataMapper,
                                PaymentRepository paymentRepository,
                                CreditEntryRepository creditEntryRepository,
                                CreditHistoryRepository creditHistoryRepository,
                                PaymentCompletedMsgPublisher paymentCompletedMsgPublisher,
                                PaymentCancelledMsgPublisher paymentCancelledMsgPublisher,
                                PaymentFailedMsgPublisher paymentFailedMsgPublisher) {
        this.paymentDomainService = paymentDomainService;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.paymentCompletedMsgPublisher = paymentCompletedMsgPublisher;
        this.paymentCancelledMsgPublisher = paymentCancelledMsgPublisher;
        this.paymentFailedMsgPublisher = paymentFailedMsgPublisher;
    }

    @Transactional
    public PaymentEvent persistPayment(PaymentRequestDTO requestDTO) {
        log.info("Received payment complete request event for order id: {}", requestDTO.getOrderId());
        Payment payment = paymentDataMapper.fromRequestDTOToPayment(requestDTO);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());

        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(
                payment, creditEntry, creditHistories, failureMessages, paymentCompletedMsgPublisher, paymentFailedMsgPublisher);
        persistObjectsToDB(payment, creditEntry, creditHistories, failureMessages);
        return paymentEvent;
    }

    @Transactional
    public PaymentEvent persistCancelPayment(PaymentRequestDTO requestDTO) {
        log.info("Received payment rollback event for order id: {}", requestDTO.getOrderId());
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(UUID.fromString(requestDTO.getOrderId()));
        if (paymentOpt.isEmpty()) {
            String errorMsg = "Payment with order id: " + requestDTO.getOrderId() +
                    " could not be found.";
            log.error(errorMsg);
            throw new PaymentDomainApplicationServiceException(errorMsg);
        }

        Payment payment = paymentOpt.get();
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> histories = getCreditHistories(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(
                payment, creditEntry, histories, failureMessages, paymentCancelledMsgPublisher, paymentFailedMsgPublisher);

        persistObjectsToDB(payment, creditEntry, histories, failureMessages);
        return paymentEvent;
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntryOpt = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntryOpt.isEmpty()) {
            String errorMsg = "Could not find credit entry for customer: " + customerId.getIdValue();
            log.error(errorMsg);
            throw new PaymentDomainApplicationServiceException(errorMsg);
        }

        return creditEntryOpt.get();
    }

    private List<CreditHistory> getCreditHistories(CustomerId customerId) {
        Optional<List<CreditHistory>> creditHistoriesOpt = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistoriesOpt.isEmpty()) {
            String errorMsg = "Could not find credit history for customer: " + customerId.getIdValue();
            log.error(errorMsg);
            throw new PaymentDomainApplicationServiceException(errorMsg);
        }

        return creditHistoriesOpt.get();
    }

    private void persistObjectsToDB(Payment payment, CreditEntry creditEntry, List<CreditHistory> creditHistories, List<String> failureMessages) {
        paymentRepository.save(payment); // payment is persisted anyway, as the status will be updated to FAILED when errors happen
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }
}
