package com.mangomelancholy.mangoai.application.conversation;

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.USER;

import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.ports.primary.ConversationService;
import com.mangomelancholy.mangoai.application.ports.secondary.AIService;
import com.mangomelancholy.mangoai.application.ports.secondary.ConversationRepository;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConversationServiceImpl implements ConversationService<Mono<ConversationEntity>,
    Mono<ExpressionValue>> {

  private final AIService aiService;
  private final ConversationRepository conversationRepository;

  public ConversationServiceImpl(final ConversationRepository conversationRepository,
      final AIService aiService) {
    this.conversationRepository = conversationRepository;
    this.aiService = aiService;
  }

  @Override
  public Mono<ConversationEntity> startConversation(final String messageContent) {
    final ExpressionValue conversationSeed = new ExpressionValue(
        "PAL is a chatbot assistant that strives to be as helpful as possible.",
        ActorType.INITIAL_PROMPT);
    final ExpressionValue palGreeting = new ExpressionValue(messageContent, USER);
    final ConversationEntity startOfConversation = new ConversationEntity(conversationSeed,
        palGreeting);
    Mono<ConversationEntity> conversationEntityMono = conversationRepository.create(
            startOfConversation.toRecord())
        .flatMap(conversationRecord -> {
          final ConversationEntity conversation = ConversationEntity.fromRecord(conversationRecord);
          return aiService.exchange(conversation)
              .map(conversation::addExpression);
        });
    return conversationEntityMono;
  }

  @Override
  public Mono<ExpressionValue> sendExpression(final String conversationId,
      final String messageContent) {
    return conversationRepository.getConversation(conversationId)
        .flatMap(conversationRecord -> {
          final ConversationEntity conversation = ConversationEntity.fromRecord(conversationRecord)
              .addExpression(new ExpressionValue(messageContent, USER));
          return aiService.exchange(conversation)
              .flatMap(responseExpression -> {
                final ConversationEntity updatedConversation = conversation.addExpression(
                    responseExpression);
                return conversationRepository
                    .update(updatedConversation.toRecord());
              });
        })
        .map(conversationRecord -> ConversationEntity.fromRecord(conversationRecord)
            .getLastExpression());
  }
}
