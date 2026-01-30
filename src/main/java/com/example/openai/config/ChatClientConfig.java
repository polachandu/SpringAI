package com.example.openai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatClientConfig {

    @Bean
    @Primary
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.defaultSystem(
                "You are an agriculture specialist. Your role is to help people with questions related to soil, season, fruits and vegetables"
                        + "If any user asks outside of these topics, inform them to consult chatgpt ")
                .defaultUser("How can you help me").build();
    }
}
