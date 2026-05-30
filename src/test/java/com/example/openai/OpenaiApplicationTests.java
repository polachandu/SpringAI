package com.example.openai;

import com.example.openai.controller.PromptStuffingController;
import org.junit.jupiter.api.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = { "spring.ai.openai.api-key=${OPENAI_API_KEY:test-key}",
        "logging.level.org.springframework.ai=DEBUG", "TAVILY_SEARCH_API_KEY=${TAVILY_SEARCH_API_KEY:test-key}" })
class OpenaiApplicationTests {

    @MockBean
    private VectorStore vectorStore;

    @Autowired
    private PromptStuffingController promptStuffingController;

    @Autowired
    private ChatModel chatModel;

    @Value("${test.relevancy.min-score:0.7}")
    private float minRelevancyScore;

    private ChatClient chatClient;
    private RelevancyEvaluator relevancyEvaluator;
    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    void setup() {
        ChatClient.Builder chatClientBuilder = ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor());
        this.chatClient = chatClientBuilder.build();
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
        this.factCheckingEvaluator = FactCheckingEvaluator.builder(chatClientBuilder).build();
    }

    @Test
    @DisplayName("Should return relevant response for basic geographic question")
    @Timeout(value = 30)
    void evaluateChatControllerResponseRelavancy() {

        String question = "What is the Capital of Canada?";

        String aiResponse = promptStuffingController.chat(question);

        EvaluationRequest evaluationRequest = new EvaluationRequest(question, aiResponse);
        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        Assertions.assertAll(() -> assertThat(aiResponse).isNotBlank(),
                () -> assertThat(evaluationResponse.isPass()).withFailMessage(
                        "The answer was not considered relevant. Question: " + question + " Response: " + aiResponse)
                        .isTrue(),
                () -> assertThat(evaluationResponse.getScore()).isGreaterThan(minRelevancyScore));
    }

    @Test
    @DisplayName("Should return factually correct response for gravity-related question")
    @Timeout(value = 30)
    void evaluateFactAccuracyForGravityQuestion() {

        String question = "Who discovered the law of universal gravitation?";

        String aiResponse = promptStuffingController.chat(question);

        EvaluationRequest evaluationRequest = new EvaluationRequest(question, aiResponse);
        EvaluationResponse factualResponse = factCheckingEvaluator.evaluate(evaluationRequest);

        Assertions.assertAll(() -> assertThat(aiResponse).isNotBlank(),
                () -> assertThat(factualResponse.isPass())
                        .withFailMessage("The answer was not considered factually correct. Question: " + question
                                + " Response: " + aiResponse)
                        .isTrue());
    }

}
