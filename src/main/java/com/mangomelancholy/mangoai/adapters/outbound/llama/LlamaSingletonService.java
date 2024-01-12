package com.mangomelancholy.mangoai.adapters.outbound.llama;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.AiSingletonService;
import com.mangomelancholy.mangoai.infrastructure.ModelInfoService.ModelType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LlamaSingletonService implements AiSingletonService {

  @Override
  public Mono<ExpressionValue> exchange(final ConversationEntity conversationEntity,
      final ModelType modelType) {
    return Mono.empty();
  }
}
