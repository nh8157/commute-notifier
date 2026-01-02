package org.commuter_notifier;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;

public class SmsClient {
    VonageClient client;
    final String phoneNumber;
    final String msgSender = "Commute";


    public SmsClient(String apiKey, String apiSecret, String phoneNumber) {
        this.client = VonageClient.builder().apiKey(apiKey).apiSecret(apiSecret).build();
        this.phoneNumber = phoneNumber;
    }

    public void sendSms(String message) throws Exception {
        TextMessage tm = new TextMessage(
            this.msgSender,
            this.phoneNumber,
            message
        );
        SmsSubmissionResponse subRes = this.client.getSmsClient().submitMessage(tm);
        MessageStatus messageStatus = subRes.getMessages().get(0).getStatus();
        if (messageStatus == MessageStatus.OK) {
            System.out.println("✅ SMS delivery: successful.");
        } else {
            throw new Exception("❌ SMS delivery failed due to %s.".formatted(messageStatus));
        }
    }
}
