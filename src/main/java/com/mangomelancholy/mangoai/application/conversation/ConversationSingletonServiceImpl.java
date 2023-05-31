package com.mangomelancholy.mangoai.application.conversation;

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.USER;

import com.mangomelancholy.mangoai.adapters.outbound.davinci.DavinciSingletonService;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.ports.primary.ConversationService;
import com.mangomelancholy.mangoai.application.ports.secondary.ConversationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConversationSingletonServiceImpl implements ConversationService<Mono<ConversationEntity>,
    Mono<ExpressionValue>> {

  private final DavinciSingletonService aiService;
  private final ConversationRepository conversationRepository;

  public ConversationSingletonServiceImpl(final ConversationRepository conversationRepository,
      final DavinciSingletonService davinciSingletonService) {
    this.conversationRepository = conversationRepository;
    this.aiService = davinciSingletonService;
  }

  @Override
  public Mono<ConversationEntity> startConversation(final String messageContent) {
    final ExpressionValue conversationSeed = new ExpressionValue(
        "You are PAL, a chatbot assistant that strives to be as helpful as possible. You prefix every response to a user with the string \"PAL: \".",
        ActorType.INITIAL_PROMPT);
    final ExpressionValue palGreeting = new ExpressionValue(messageContent, USER);
    final ConversationEntity startOfConversation = new ConversationEntity(conversationSeed,
        palGreeting);
    return conversationRepository.create(
            startOfConversation.toRecord())
        .flatMap(conversationRecord -> {
          final ConversationEntity conversation = ConversationEntity.fromRecord(conversationRecord);
          return aiService.exchange(conversation)
              .map(conversation::addExpression);
        });
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
