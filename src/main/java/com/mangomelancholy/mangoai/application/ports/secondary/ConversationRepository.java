package com.mangomelancholy.mangoai.application.ports.secondary;

import reactor.core.publisher.Mono;

public interface ConversationRepository {
  Mono<ConversationRecord> create(ConversationRecord newConversation);
  Mono<ConversationRecord> getConversation(String conversationId);
  Mono<ConversationRecord> update(ConversationRecord conversation);

}

