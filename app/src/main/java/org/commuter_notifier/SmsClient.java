package org.commuter_notifier;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;

public class SmsClient {
    VonageClient client;
    String phoneNumber;

    public SmsClient(String apiKey, String apiSecret, String phoneNumber) {
        this.client = VonageClient.builder().apiKey(apiKey).apiSecret(apiSecret).build();
        this.phoneNumber = phoneNumber;
    }

    public void sendSms(String message) {
        TextMessage tm = new TextMessage(
            "Commute Notification",
            this.phoneNumber,
            message
        );
        SmsSubmissionResponse subRes = this.client.getSmsClient().submitMessage(tm);
        MessageStatus messageStatus = subRes.getMessages().get(0).getStatus();
        if (messageStatus == MessageStatus.OK) {
            System.out.println("SMS delivery successful.");
        } else {
            System.out.println("SMS delivery failed: %s.".formatted(messageStatus));
        }
    }
}
