package com.ox.crm.core.configuration;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ox.crm.core.filters.WebSocketAuthenticationFilter;
import com.ox.crm.core.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final ApplicationProperties properties;
  private final WebSocketAuthenticationFilter authenticationFilter;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOrigins(properties.getCors()
            .getAllowedOrigins()
            .toArray(new String[] {}))
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(authenticationFilter);
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
}
