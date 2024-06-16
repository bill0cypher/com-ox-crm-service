package com.ox.crm.core.controller;

import static java.util.UUID.fromString;

import com.ox.crm.core.dto.ClientDto;
import com.ox.crm.core.dto.param.ClientCreateParam;
import com.ox.crm.core.dto.param.ClientUpdateParam;
import com.ox.crm.core.mapper.ClientMapper;
import com.ox.crm.core.service.ClientService;
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
@RequestMapping("/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Clients resource")
public class ClientController {

  private final ClientService clientService;
  private final ClientMapper clientMapper;

  @PostMapping
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public ClientDto createClient(@Valid @RequestBody ClientCreateParam clientParam) {
    var clientMapped = clientMapper.mapToClient(clientParam);
    var client = clientService.create(clientMapped);

    return clientMapper.mapToClientDto(client);
  }

  @PatchMapping("{clientId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public ClientDto createClient(
      @PathVariable @UUID String clientId,
      @RequestBody @Valid ClientUpdateParam clientParam) {
    var client = clientService.update(fromString(clientId), clientParam);

    return clientMapper.mapToClientDto(client);
  }

  @DeleteMapping("{clientId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "Accepted"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteClient(@PathVariable @UUID String clientId) {
    clientService.delete(fromString(clientId));
  }

  @GetMapping("{clientId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public ClientDto findClientById(@PathVariable @UUID String clientId) {
    var client = clientService.findById(fromString(clientId));

    return clientMapper.mapToClientDto(client);
  }

  @GetMapping
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public Page<ClientDto> findAllClients(Pageable pageable) {
    return clientService.findAll(pageable);
  }
}
