package com.ox.crm.core.mapper;

import com.ox.crm.core.dto.ContactDto;
import com.ox.crm.core.dto.param.ContactCreateParam;
import com.ox.crm.core.dto.param.ContactUpdateParam;
import com.ox.crm.core.model.Contact;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface ContactMapper {
  ContactDto mapToContactDto(Contact contact);

  @Mapping(target = "clientId", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "tasks", ignore = true)
  Contact mapToContact(ContactCreateParam contactCreateParam);

  @Mapping(target = "clientId", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "tasks", ignore = true)
  Contact mapToContact(ContactCreateParam contactCreateParam, String password);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "clientId", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "tasks", ignore = true)
  @Mapping(target = "password", ignore = true)
  Contact updateContact(ContactUpdateParam contactUpdateParam, @MappingTarget Contact contact);

}
