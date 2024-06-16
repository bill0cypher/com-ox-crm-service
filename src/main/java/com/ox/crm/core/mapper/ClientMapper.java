package com.ox.crm.core.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.ox.crm.core.dto.ClientDto;
import com.ox.crm.core.dto.param.AddressUpdateParam;
import com.ox.crm.core.dto.param.ClientCreateParam;
import com.ox.crm.core.dto.param.ClientUpdateParam;
import com.ox.crm.core.model.Address;
import com.ox.crm.core.model.Client;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper()
public interface ClientMapper {

  ClientDto mapToClientDto(Client client);

  Client mapToClient(ClientCreateParam clientCreateParam);

  @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
  @Mapping(source = "address", target = "address", qualifiedByName = "updateAddress", nullValuePropertyMappingStrategy = IGNORE)
  Client updateClient(ClientUpdateParam clientUpdateParam, @MappingTarget Client client);

  @Named("updateAddress")
  Address updateAddress(AddressUpdateParam address);
}
