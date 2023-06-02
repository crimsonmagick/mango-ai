package com.mangomelancholy.mangoai.application.ports.secondary;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConversationRepository {
  Mono<ConversationRecord> create(ConversationRecord newConversation);
  Mono<ConversationRecord> getConversation(String conversationId);

  Flux<String> getConversationIds();

  Flux<ExpressionRecord> getExpressions(String conversationId);
  Mono<ConversationRecord> update(ConversationRecord conversation);

}

