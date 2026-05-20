package com.example.openai.rag;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WebSearchDocumentRetriever implements DocumentRetriever {

    private static final String TAVILY_API_KEY = "TAVILY_SEARCH_API_KEY";
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
        log.info("Processing query : {} ", query.text());

        String q = query.text();
        ;

        TaviltyResponsePayload responsePayload = restClient.post()
                .body(new TavilyRequestPayload(q, "advanced", resultLimit)).retrieve()
                .body(TaviltyResponsePayload.class);

        if (responsePayload == null || CollectionUtils.isEmpty(responsePayload.results())) {
            return List.of();
        }

        List<Document> docs = new ArrayList<>(responsePayload.results().size());
        for (TaviltyResponsePayload.Hit hit : responsePayload.results()) {
            Document doc = Document.builder().text(hit.content()).metadata("title", hit.title())
                    .metadata("url", hit.url()).score(hit.score()).build();
            docs.add(doc);
        }
        return docs;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    record TavilyRequestPayload(String query, String searchDepth, int maxResults) {
    }

    record TaviltyResponsePayload(List<Hit> results) {
        record Hit(String title, String url, String content, Double score) {
        }
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

        public Builder maxResults(int maxResults) {
            if (maxResults < 0) {
                throw new IllegalArgumentException("max results should be greater than 0");
            }
            this.resultLimit = maxResults;
            return this;
        }

        public WebSearchDocumentRetriever build() {
            return new WebSearchDocumentRetriever(resultLimit, clientBuilder);
        }
    }
}
