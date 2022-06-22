package com.example.notificationservice.senders;

import java.io.IOException;

public interface Sender {
  Boolean sendNotification(String personContact, String subject, String body) throws IOException;
}
