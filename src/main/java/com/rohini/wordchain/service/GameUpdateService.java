
package com.rohini.wordchain.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.rohini.wordchain.dto.GameStatusResponse;

@Service
public class GameUpdateService {

    private final SimpMessagingTemplate messagingTemplate;

    public GameUpdateService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastGameStatus(
            String roomCode,
            GameStatusResponse status) {

        messagingTemplate.convertAndSend(
                "/topic/games/" + roomCode,
                status
        );
    }
}
