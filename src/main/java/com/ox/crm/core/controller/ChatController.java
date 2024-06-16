package com.ox.crm.core.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ox.crm.core.dto.ChatMessage;
import com.ox.crm.core.dto.OutputChatMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Tag(name = "WS Chat", description = "Chat topic resource")
public class ChatController {

  private final SimpMessagingTemplate simpMessagingTemplate;

  @MessageMapping("/chat/{uuid}")
  public void send(@DestinationVariable("uuid") String uuid, @Payload ChatMessage payload) {
    final String time = new SimpleDateFormat("HH:mm").format(new Date());

    simpMessagingTemplate.convertAndSend("/topic/messages/" + uuid, new OutputChatMessage(payload.getFrom(), payload.getText(), time));
  }
}
