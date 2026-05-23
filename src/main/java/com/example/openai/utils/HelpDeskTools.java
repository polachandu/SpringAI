package com.example.openai.utils;

import com.example.openai.entity.HelpDeskTicket;
import com.example.openai.model.TicketRequest;
import com.example.openai.service.HelpDeskTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HelpDeskTools {

    private final HelpDeskTicketService helpDeskTicketService;

    @Tool(name = "createTicket", description = "Create the support ticket")
    String createTicket(@ToolParam(description = "Details to create a support ticket") TicketRequest ticketRequest,
            ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");
        HelpDeskTicket savedTicket = helpDeskTicketService.createTicket(ticketRequest, username);
        return "Ticket #" + savedTicket.getId() + " created successfully for user " + savedTicket.getUsername();
    }

    @Tool(description = "Fetch the status of open tickets based on the username")
    List<HelpDeskTicket> getTicketStatus(ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");
        return helpDeskTicketService.getTicketsByUsername(username);
    }
}
