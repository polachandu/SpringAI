package com.example.openai.config;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.example.advisor.TokenUsageAuditAdvisor;

@Configuration
public class ChatClientConfig {

    @Bean
    @Primary
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        ChatOptions options = ChatOptions.builder().model("gpt-5.2").temperature(0.8).build();
        return chatClientBuilder.defaultOptions(options)
        .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), new TokenUsageAuditAdvisor()))
        .defaultSystem(
                "You are an agriculture specialist. Your role is to help people with questions related to soil, season, fruits and vegetables"
                        + "If any user asks outside of these topics, inform them to consult chatgpt ")
                .defaultUser("How can you help me").build();
    }
}
