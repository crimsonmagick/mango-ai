package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.ports.secondary.AIService;
import com.mangomelancholy.mangoai.infrastructure.OpenAICompletionsClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class DavinciService implements AIService {

  private static final Logger log = LogManager.getLogger(DavinciService.class);
  private final OpenAICompletionsClient completionsClient;
  private final ConversationSerializer conversationSerializer;

  public DavinciService(final OpenAICompletionsClient completionsClient, final ConversationSerializer expressionSerializer) {
    this.completionsClient = completionsClient;
    this.conversationSerializer = expressionSerializer;
  }

  @Override
  public Mono<ExpressionValue> exchange(final ConversationEntity conversationEntity) {
    final String content = conversationSerializer.serializeConversation(conversationEntity);
    return completionsClient.complete(content)
        .doOnError(throwable -> {
          final String responseBody;
          if (throwable instanceof WebClientResponseException) {
            responseBody = ((WebClientResponseException) throwable).getResponseBodyAsString();
          } else {
            responseBody = "NONE";
          }
          log.error("Error while sending expression content. content={}, responseBody={}", content, responseBody, throwable);
        })
        .map(response -> {
          // FIXME NPE extravaganza
          final String choiceText = response.choices().get(0).text();
          final String palResponse = choiceText.substring(choiceText.lastIndexOf("PAL: "));
          return new ExpressionValue(palResponse, ActorType.PAL);
        });
  }
}
