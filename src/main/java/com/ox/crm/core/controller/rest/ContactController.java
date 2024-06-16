package com.ox.crm.core.controller.rest;

import static java.util.UUID.fromString;

import com.ox.crm.core.dto.ContactDto;
import com.ox.crm.core.dto.param.ContactCreateParam;
import com.ox.crm.core.dto.param.ContactUpdateParam;
import com.ox.crm.core.mapper.ContactMapper;
import com.ox.crm.core.service.ContactService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/v1/contacts")
@RequiredArgsConstructor
@Tag(name = "Contacts", description = "Contacts resource")
public class ContactController {
  
  private final ContactService contactService;
  private final ContactMapper contactMapper;

  @PostMapping
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public ContactDto createContact(@Valid @RequestBody ContactCreateParam contactParam) {
    var contact = contactService.create(contactParam);

    return contactMapper.mapToContactDto(contact);
  }

  @PatchMapping("{contactId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public ContactDto updateContact(
      @PathVariable @UUID String contactId,
      @RequestBody @Valid ContactUpdateParam contactParam) {
    var contact = contactService.update(fromString(contactId), contactParam);

    return contactMapper.mapToContactDto(contact);
  }

  @DeleteMapping("{contactId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "Accepted"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteContact(@PathVariable @UUID String contactId) {
    contactService.delete(fromString(contactId));
  }

  @GetMapping("{contactId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public ContactDto findContactById(@PathVariable @UUID String contactId) {
    var contact = contactService.findById(fromString(contactId));

    return contactMapper.mapToContactDto(contact);
  }

  @GetMapping
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public Page<ContactDto> findAllContacts(Pageable pageable) {
    return contactService.findAll(pageable);
  }
}
