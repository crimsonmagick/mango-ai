package com.mangomelancholy.mangoai.application.conversation;

import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.conversation.repository.ConversationRecord;
import com.mangomelancholy.mangoai.application.conversation.repository.ConversationRepository;
import reactor.core.publisher.Mono;

public class ConversationServiceImpl implements ConversationService {

  private final ConversationRepository conversationRepository;

  public ConversationServiceImpl(final ConversationRepository conversationRepository) {
    this.conversationRepository = conversationRepository;
  }

  @Override
  public Mono<ConversationEntity> startConversation(final String message) {
    final ExpressionValue expression = new ExpressionValue(message, ActorType.USER);
    final ConversationEntity startOfConversation = new ConversationEntity(expression);
    final Mono<ConversationRecord> conversationRecordMono = conversationRepository.create(startOfConversation.toRecord());
    return null;
  }
}
