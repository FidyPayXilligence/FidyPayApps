package com.fidypay;

import java.util.HashMap;
import java.util.Map;

//Define the service interface
interface MessageService {
 void sendMessage(String message, String recipient);
}

//Implement the service for email
class EmailService implements MessageService {
 @Override
 public void sendMessage(String message, String recipient) {
     System.out.println("Sending email to " + recipient + ": " + message);
     // Code to send email using an email library
 }
}

//Implement the service for SMS
class SmsService implements MessageService {
 @Override
 public void sendMessage(String message, String recipient) {
     System.out.println("Sending SMS to " + recipient + ": " + message);
     // Code to send SMS using an SMS gateway
 }
}

//Custom Service Resolver
class MessageServiceResolver {
 private static final Map<String, MessageService> services = new HashMap<>();

 // Register the services
 static {
     services.put("email", new EmailService());
     services.put("sms", new SmsService());
 }

 // Method to get the service
 public static MessageService getService(String type) {
     MessageService service = services.get(type);
     if (service == null) {
         throw new IllegalArgumentException("Service type not supported: " + type);
     }
     return service;
 }
}

//Client code
public class NotificationApp {
 public static void main(String[] args) {
     // Use the Service Resolver to get the desired service
     MessageService emailService = MessageServiceResolver.getService("email");
     emailService.sendMessage("Hello John!", "john.doe@example.com");

     MessageService smsService = MessageServiceResolver.getService("sms");
     smsService.sendMessage("Meeting reminder", "+1234567890");

     //Trying to get a service that does not exist.
     try{
         MessageService wrongService = MessageServiceResolver.getService("wrong");
     }
     catch(IllegalArgumentException e){
         System.out.println(e.getMessage());
     }

 }
}

