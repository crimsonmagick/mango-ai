package com.mangomelancholy.mangoai.application.conversation;

import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.conversation.repository.ConversationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConversationServiceImpl implements ConversationService {

  private final ConversationRepository conversationRepository;

  public ConversationServiceImpl(final ConversationRepository conversationRepository) {
    this.conversationRepository = conversationRepository;
  }

  @Override
  public Mono<ConversationEntity> startConversation() {
    final ExpressionValue conversationSeed = new ExpressionValue("PAL is a chatbot assistant.", ActorType.SYSTEM);
    final ExpressionValue palGreeting = new ExpressionValue("Hi there, I'm PAL! How may I assist you?", ActorType.PAL);
    final ConversationEntity startOfConversation = new ConversationEntity(conversationSeed, palGreeting);
    return conversationRepository.create(startOfConversation.toRecord())
      .map(ConversationEntity::fromRecord);
  }
}
