package com.example.openai.rag;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class PIMaskingDocumentPostProcessor implements DocumentPostProcessor {

    private PIMaskingDocumentPostProcessor() {
    }

    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "\\b(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}\\b", Pattern.CASE_INSENSITIVE);

    private static final String EMAIL_REPLACEMENT = "[REDACTED_EMAIL]";
    private static final String PHONE_REPLACEMENT = "[REDACTED_PHONE]";

    @Override
    public List<Document> process(Query query, List<Document> documents) {
        if (CollectionUtils.isEmpty(documents)) {
            return documents;
        }
        log.debug("Masking sensitive information in documents for query : {}", query.text());

        return documents.stream().map(document -> {
            String text = document.getText() != null ? document.getText() : "";
            String maskedText = maskSensitivieInformataion(text);
            return document.mutate().text(maskedText).metadata("pi_masked", true).build();
        }).toList();
    }

    private String maskSensitivieInformataion(String text) {
        String masked = text;
        masked = EMAIL_PATTERN.matcher(masked).replaceAll(EMAIL_REPLACEMENT);
        masked = PHONE_PATTERN.matcher(masked).replaceAll(PHONE_REPLACEMENT);
        return masked;
    }

    public static PIMaskingDocumentPostProcessor builder() {
        return new PIMaskingDocumentPostProcessor();
    }
}
