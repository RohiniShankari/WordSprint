
package com.rohini.wordchain.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BrevoEmailService {

    private final RestClient restClient;

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name}")
    private String senderName;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public BrevoEmailService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .build();
    }
    public void sendInvitation(
            String recipientEmail,
            String roomCode,
            String hostUsername) {

        String joinLink = frontendUrl + "/join/" + roomCode;

        Map<String, Object> sender = Map.of(
                "name", senderName,
                "email", senderEmail
        );

        Map<String, String> recipient = Map.of(
                "email", recipientEmail
        );

        Map<String, Object> requestBody = Map.of(
                "sender", sender,
                "to", List.of(recipient),
                "subject", hostUsername + " invited you to Word Chain!",
                "textContent", """
                        Hi!

                        %s has invited you to play Word Chain.

                        Room code: %s

                        Join the game here: %s

                        See you there!
                        """.formatted(hostUsername, roomCode, joinLink)
        );

        restClient.post()
                .uri("/smtp/email")
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }
}
