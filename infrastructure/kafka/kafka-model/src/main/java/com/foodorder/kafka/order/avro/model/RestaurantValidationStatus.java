/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.foodorder.kafka.order.avro.model;
@org.apache.avro.specific.AvroGenerated
public enum RestaurantValidationStatus implements org.apache.avro.generic.GenericEnumSymbol<RestaurantValidationStatus> {
  APPROVED, REJECTED  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"RestaurantValidationStatus\",\"namespace\":\"com.foodorder.kafka.order.avro.model\",\"symbols\":[\"APPROVED\",\"REJECTED\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
