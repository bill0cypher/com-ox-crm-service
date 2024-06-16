package com.ox.crm.core.service;

import static com.ox.crm.core.constants.AppConstants.Logging.CLIENT_ID;

import java.util.UUID;

import com.ox.crm.core.dto.ClientDto;
import com.ox.crm.core.dto.param.ClientUpdateParam;
import com.ox.crm.core.exception.NotFoundException;
import com.ox.crm.core.mapper.ClientMapper;
import com.ox.crm.core.model.Client;
import com.ox.crm.core.repository.AddressRepository;
import com.ox.crm.core.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {
  private final ClientRepository clientRepository;
  private final AddressRepository addressRepository;
  private final ClientMapper clientMapper;

  public Client create(Client client) {
    return clientRepository.save(client);
  }

  @CacheEvict(value = "clientsCache", key = "#clientId", cacheManager = "cacheManager")
  public Client update(UUID clientId, ClientUpdateParam clientParam) {
    var client = findById(clientId);
    var updateClientMapped = clientMapper.updateClient(clientParam, client);
    var updatedClient = clientRepository.save(updateClientMapped);

    log.info("Client updated: {}={}", CLIENT_ID, clientId);

    return updatedClient;
  }

  @CacheEvict(value = "clientsCache", key = "#clientId", cacheManager = "cacheManager")
  public void delete(UUID clientId) {
    var client = findById(clientId);

    clientRepository.delete(client);

    log.info("Client deleted: {}={}", CLIENT_ID, clientId);
  }

  @Cacheable(value = "clientsCache", key = "#clientId", cacheManager = "cacheManager")
  public Client findById(UUID clientId) {
    return clientRepository.findById(clientId)
        .orElseThrow(() -> {
          log.error("Client with id {}={} not found", CLIENT_ID, clientId);
          return new NotFoundException();
        });
  }

  public Page<ClientDto> findAll(Pageable pageable) {
    var clients = clientRepository.findAll(pageable);
    var clientsMapped = clients.getContent()
        .stream()
        .map(clientMapper::mapToClientDto)
        .toList();

    return PageableExecutionUtils.getPage(clientsMapped, clients.getPageable(), clients::getTotalElements);
  }
}
