package com.mangomelancholy.mangoai.adapters.outbound;

import com.mangomelancholy.mangoai.application.ports.secondary.ConversationRecord;
import com.mangomelancholy.mangoai.application.ports.secondary.ConversationRepository;
import com.mangomelancholy.mangoai.application.ports.secondary.ExpressionRecord;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ConversationRepositoryImpl implements ConversationRepository {

  final Map<String, ConversationRecord> conversations;

  public ConversationRepositoryImpl() {
    conversations = new ConcurrentHashMap<>();
  }

  @Override
  public Mono<ConversationRecord> create(final ConversationRecord newConversation) {
    final String conversationId = UUID.randomUUID().toString();
    final ConversationRecord newRecord = new ConversationRecord(conversationId, newConversation.expressions());
    conversations.put(conversationId, newRecord);
    return Mono.just(newRecord);
  }

  @Override
  public Mono<ConversationRecord> getConversation(final String conversationId) {
    final ConversationRecord conversationRecord = conversations.get(conversationId);
    if (conversationRecord == null) {
      return Mono.empty();
    }
    return Mono.just(conversationRecord);
  }

  @Override
  public Flux<ExpressionRecord> getExpressions(final String conversationId) {
    ConversationRecord conversationRecord = conversations.get(conversationId);
    if (conversationRecord == null) {
      return Flux.empty();
    } else {
      return Flux.fromIterable(conversationRecord.expressions());
    }
  }

  @Override
  public Mono<ConversationRecord> update(final ConversationRecord conversation) {
    conversations.put(conversation.conversationId(), conversation);
    return Mono.just(conversation);
  }
}
