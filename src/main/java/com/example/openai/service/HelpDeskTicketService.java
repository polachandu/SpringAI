package com.example.openai.service;

import com.example.openai.entity.HelpDeskTicket;
import com.example.openai.model.TicketRequest;
import com.example.openai.repository.HelpDeskTicketRepository;
import com.example.openai.utils.Status;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HelpDeskTicketService {

    private final HelpDeskTicketRepository helpDeskTicketRepository;

    public HelpDeskTicket createTicket(TicketRequest ticketRequest, String username) {

        HelpDeskTicket ticket = HelpDeskTicket.builder().issue(ticketRequest.issue()).username(username)
                .status(Status.OPEN.toString()).createdAt(LocalDateTime.now()).eta(LocalDateTime.now().plusDays(7))
                .build();
        return helpDeskTicketRepository.save(ticket);
    }

    public List<HelpDeskTicket> getTicketsByUsername(String username) {
        return helpDeskTicketRepository.findByUsername(username);
    }
}
