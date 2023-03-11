package com.mangomelancholy.mangoai.application.conversation.repository;

import reactor.core.publisher.Mono;

public interface ConversationRepository {
  Mono<ConversationRecord> create(ConversationRecord newConversation);
  Mono<ConversationRecord> getConversation(String conversationId);
  Mono<Void> update(ConversationRecord conversation);

}
