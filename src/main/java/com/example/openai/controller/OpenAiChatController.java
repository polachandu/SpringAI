package com.example.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.advisor.TokenUsageAuditAdvisor;

@RestController
@RequestMapping("/openaiapi")
public class OpenAiChatController {

    @Autowired
    @Qualifier("chatClient")
    private ChatClient chatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message) {
        return chatClient.prompt().advisors(new TokenUsageAuditAdvisor()).user(message).call().content();
    }
}
