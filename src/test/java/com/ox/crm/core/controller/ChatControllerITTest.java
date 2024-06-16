package com.ox.crm.core.controller;

import static java.util.concurrent.TimeUnit.SECONDS;

import static com.ox.crm.core.constants.AppConstants.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ox.crm.core.configuration.WebSocketConfig;
import com.ox.crm.core.dto.ChatMessage;
import com.ox.crm.core.dto.OutputChatMessage;
import com.ox.crm.core.repository.ContactRepository;
import com.ox.crm.core.repository.PrivilegeRepository;
import com.ox.crm.core.repository.RoleRepository;
import com.ox.crm.core.repository.TaskRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = """
    server.port=8080
    server.servlet.context-path=
    """)
@TestPropertySource("classpath:application-test.yaml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import(value = {WebSocketConfig.class})
public class ChatControllerITTest extends BaseControllerITTest {

  private static final String SEND_CHAT_ENDPOINT = "/app/chat/";
  private static final String SUBSCRIBE_CHAT_ENDPOINT = "/topic/messages/";

  @LocalServerPort
  private int port;
  private CompletableFuture<OutputChatMessage> completableFuture;
  private String url;

  @Autowired
  ChatControllerITTest(
      BCryptPasswordEncoder passwordEncoder,
      ObjectMapper objectMapper,
      PrivilegeRepository privilegeRepository,
      RoleRepository roleRepository,
      ContactRepository contactRepository,
      TaskRepository taskRepository,
      MockMvc mockMvc) {
    super(passwordEncoder, objectMapper, privilegeRepository, roleRepository, contactRepository, taskRepository, mockMvc);
  }

  @BeforeEach
  public void setUp() {
    completableFuture = new CompletableFuture<>();
    url = "ws://localhost:" + port + "/ws";
  }

  @Test
  @SneakyThrows
  public void shouldSendMessage() {
    setup();
    String taskUuid = UUID.randomUUID().toString();
    var stompHeader = new StompHeaders();
    var authHeader = new HttpHeaders();
    var mapper = new MappingJackson2MessageConverter();
    mapper.setContentTypeResolver(new DefaultContentTypeResolver());
    mapper.setObjectMapper(objectMapper);

    var payload = new ChatMessage(principalContact.getEmail(), "Hello there");
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
    stompHeader.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
    stompClient.setMessageConverter(mapper);

    StompSession stompSession = stompClient.connectAsync(url,
        new WebSocketHttpHeaders(authHeader),
        stompHeader,
        new StompSessionHandlerAdapter() {}).get(20, SECONDS);

    stompSession.subscribe(SUBSCRIBE_CHAT_ENDPOINT + taskUuid, new ChatPayloadStompFrameHandler());
    stompSession.send(SEND_CHAT_ENDPOINT + taskUuid, payload);

    OutputChatMessage chatState = completableFuture.get(20, SECONDS);

    assertNotNull(chatState);
  }

  private List<Transport> createTransportClient() {
    List<Transport> transports = new ArrayList<>(1);
    transports.add(new WebSocketTransport(new StandardWebSocketClient()));
    return transports;
  }

  private class ChatPayloadStompFrameHandler implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
      return OutputChatMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
      completableFuture.complete((OutputChatMessage) o);
    }
  }
}
