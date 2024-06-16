package com.ox.crm.core.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
@Entity(name = "address")
@AllArgsConstructor
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String country;
  private String city;
  private String addressLine1;
  private String addressLine2;
  private String state;
  private String zipCode;
}
