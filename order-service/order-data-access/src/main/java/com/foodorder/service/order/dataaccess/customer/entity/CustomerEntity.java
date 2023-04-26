package com.foodorder.service.order.dataaccess.customer.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor // for proxying with reflection
@AllArgsConstructor // for Builder pattern
@Table(name = "order_customers_m_view", schema = "customer")
@Entity
public class CustomerEntity {
    @Id
    private UUID id;
}