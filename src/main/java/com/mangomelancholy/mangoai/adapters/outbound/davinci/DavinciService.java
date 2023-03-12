package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.ports.secondary.AIService;
import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.infrastructure.OpenAICompletionsClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DavinciService implements AIService {

  private final OpenAICompletionsClient completionsClient;

  public DavinciService(final OpenAICompletionsClient completionsClient) {
   this.completionsClient = completionsClient;
  }

  @Override
  public Mono<ExpressionValue> exchange(final ConversationEntity conversationEntity) {
    return Mono.empty();
  }
}
