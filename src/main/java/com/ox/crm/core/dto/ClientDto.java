package com.ox.crm.core.dto;

import com.ox.crm.core.model.Address;
import lombok.Data;

@Data
public class ClientDto {
  private String companyName;
  private String industry;
  private Address address;
}
