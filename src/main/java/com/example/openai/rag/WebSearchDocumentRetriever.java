package com.example.openai.rag;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
public class WebSearchDocumentRetriever implements DocumentRetriever {

    private static final String TAVILY_API_KEY = "TAVILTY_SEARCH_API_KEY";
    private static final String TAVILY_BASE_URL = "https://api.tavily.com/search";
    private static final int DEFAULT_RESULT_LIMIT = 5;
    private final int resultLimit;
    private final RestClient restClient;

    public WebSearchDocumentRetriever(int resultLimit, RestClient.Builder restClientBuilder) {
        this.resultLimit = resultLimit;
        String apiKey = System.getenv(TAVILY_API_KEY);
        Assert.hasText(apiKey, "Environment variable " + TAVILY_API_KEY + " must be set");
        this.restClient = restClientBuilder.baseUrl(TAVILY_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey).build();
        if (resultLimit <= 0) {
            throw new IllegalArgumentException("result limit must be greater than 0");
        }
    }

    @Override
    public List<Document> retrieve(Query query) {
        return List.of();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RestClient.Builder clientBuilder;
        private int resultLimit = DEFAULT_RESULT_LIMIT;

        private Builder() {
        }

        public Builder restClientBuilder(RestClient.Builder clientBuilder) {
            this.clientBuilder = clientBuilder;
            return this;
        }
    }
}
