package com.mangomelancholy.mangoai.adapters.outbound;

import com.mangomelancholy.mangoai.application.conversation.repository.ConversationRecord;
import com.mangomelancholy.mangoai.application.conversation.repository.ConversationRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ConversationRepositoryImpl implements ConversationRepository {

  public ConversationRepositoryImpl() {
    conversations = new HashMap<>();
  }

  final Map<String, ConversationRecord> conversations;

  @Override
  public Mono<ConversationRecord> create(final ConversationRecord newConversation) {
    final String conversationId = UUID.randomUUID().toString();
    final ConversationRecord  newRecord = new ConversationRecord(conversationId, newConversation.expressions());
    conversations.put(conversationId,  newRecord);
    return Mono.just( newRecord);
  }

  @Override
  public Mono<ConversationRecord> getConversation(final String conversationId) {
    return Mono.just(conversations.get(conversationId));
  }

  @Override
  public Mono<Void> update(final ConversationRecord conversation) {
    conversations.put(conversation.conversationId(), conversation);
    return Mono.empty();
  }
}
