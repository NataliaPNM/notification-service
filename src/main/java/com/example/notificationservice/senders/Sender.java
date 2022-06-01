package com.example.notificationservice.senders;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Sender {
     String sendCode(String contact, String code) throws IOException;
}
