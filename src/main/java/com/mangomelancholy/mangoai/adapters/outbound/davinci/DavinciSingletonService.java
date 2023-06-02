package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.application.ports.secondary.AISingletonService;
import com.mangomelancholy.mangoai.infrastructure.OpenAICompletionsClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class DavinciSingletonService implements AISingletonService {

  private static final Logger log = LogManager.getLogger(DavinciSingletonService.class);
  private final CompletionUtility completionUtility;
  private final OpenAICompletionsClient completionsClient;
  private final ConversationSerializer conversationSerializer;


  public DavinciSingletonService(final OpenAICompletionsClient completionsClient,
      final ConversationSerializer conversationSerializer,
      final CompletionUtility completionUtility) {
    this.completionsClient = completionsClient;
    this.conversationSerializer = conversationSerializer;
    this.completionUtility = completionUtility;
  }

  @Override
  public Mono<ExpressionValue> exchange(final ConversationEntity conversationEntity) {
    final String content = conversationSerializer.serializeConversation(conversationEntity);
    return completionsClient.singleton().complete(content)
        .doOnError(throwable -> onError(throwable, content))
        .map(response -> {
          if (response == null || response.choices() == null || response.choices().size() < 1
              || response.choices().get(0) == null || response.choices().get(0).text() == null) {
            log.error("Invalid response, response={}", response);
            throw new RuntimeException(String.format("Invalid response, response=%s", response));
          }
          return completionUtility.mapExpressionValue(response);
        });
  }

  private void onError(final Throwable throwable, final String content) {
    final String responseBody;
    if (throwable instanceof WebClientResponseException) {
      responseBody = ((WebClientResponseException) throwable).getResponseBodyAsString();
    } else {
      responseBody = "NONE";
    }
    log.error("Error while sending expression content. content={}, responseBody={}", content,
        responseBody, throwable);

  }
}
