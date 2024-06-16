package com.ox.crm.core.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ox.crm.core.dto.ChatMessage;
import com.ox.crm.core.dto.OutputChatMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Tag(name = "WS Chat", description = "Chat topic resource")
public class ChatController {

  @MessageMapping("/chat")
  @SendTo("/topic/messages")
  public OutputChatMessage send(final Message<ChatMessage> message) {
    final String time = new SimpleDateFormat("HH:mm").format(new Date());
    final ChatMessage payload = message.getPayload();

    return new OutputChatMessage(payload.getFrom(), payload.getText(), time);
  }

}
