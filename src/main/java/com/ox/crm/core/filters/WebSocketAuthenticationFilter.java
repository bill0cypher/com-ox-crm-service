package com.ox.crm.core.filters;

import static com.ox.crm.core.constants.AppConstants.BEARER_PREFIX;

import com.ox.crm.core.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSocketAuthenticationFilter implements ChannelInterceptor {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
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
}
