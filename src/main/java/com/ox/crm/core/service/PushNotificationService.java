package com.ox.crm.core.service;

import com.ox.crm.core.dto.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushNotificationService {
  private final SimpMessagingTemplate simpMessagingTemplate;

  public void pushNotification(Notification notification) {
    //TODO: Send notifications on task assign / create
  }
}
