package com.example.openai.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "helpdesk_ticket")
public class HelpDeskTicket {

    @Id
    private Long id;
    private String username;
    private String issue;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime eta;
}
