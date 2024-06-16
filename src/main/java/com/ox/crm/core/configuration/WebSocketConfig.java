package com.ox.crm.core.configuration;

import static com.ox.crm.core.constants.AppConstants.BEARER_PREFIX;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ox.crm.core.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public WebSocketConfig(JwtService jwtService, @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").withSockJS();
  }

  @Override
  public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    var resolver = new DefaultContentTypeResolver();
    resolver.setDefaultMimeType(APPLICATION_JSON);
    var converter = new MappingJackson2MessageConverter();
    converter.setObjectMapper(new ObjectMapper());
    converter.setContentTypeResolver(resolver);
    messageConverters.add(converter);

    return false;
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("Headers: {}", accessor);

        assert accessor != null;
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

          String authorizationHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
          assert authorizationHeader != null;
          String token = authorizationHeader.substring(BEARER_PREFIX.length());
          String username = jwtService.extractUsername(token);
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          if (!jwtService.isTokenValid(token, userDetails)) {
            log.warn("User session expired. Stop message propagation");
            return null;
          }
        }

        return message;
      }

    });
  }
}
