package com.example.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/springTemplate")
public class PromptTemplateController {

    @Autowired
    private ChatClient chatClient;

    @Value("classpath:/promptTemplates/UserPromptTemplate.st")
    Resource userPromptTemplate;

    @GetMapping("/email")
    public String emailResponse(@RequestParam String customerName, @RequestParam String customerMessage) {
        return chatClient.prompt()
                .system("You are a professional customer service assistance which"
                        + " helps in drafting email responses to improve productivity of customer support team")
                .user(promptTemplateSpec -> promptTemplateSpec.text(userPromptTemplate)
                        .param("customerName", customerName).param("customerMessage", customerMessage))
                .call().content();
    }
}
