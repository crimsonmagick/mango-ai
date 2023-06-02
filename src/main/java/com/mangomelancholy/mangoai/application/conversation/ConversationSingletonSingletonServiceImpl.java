package com.mangomelancholy.mangoai.application.conversation;

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.USER;

import com.mangomelancholy.mangoai.adapters.outbound.davinci.DavinciSingletonService;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.ports.primary.ConversationNotFound;
import com.mangomelancholy.mangoai.application.ports.primary.ConversationSingletonService;
import com.mangomelancholy.mangoai.application.ports.secondary.ConversationRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConversationSingletonSingletonServiceImpl implements ConversationSingletonService {

  private final ConversationRepository conversationRepository;
  private final DavinciSingletonService davinciSingletonService;

  public ConversationSingletonSingletonServiceImpl(final ConversationRepository conversationRepository,
      final DavinciSingletonService davinciSingletonService) {
    this.conversationRepository = conversationRepository;
    this.davinciSingletonService = davinciSingletonService;
  }

  @Override
  public Mono<List<ExpressionValue>> getExpressions(final String conversationId) {
    return conversationRepository.getExpressions(conversationId)
        .switchIfEmpty(Mono.error(new ConversationNotFound(conversationId)))
        .map(ExpressionValue::fromRecord)
        .collect(Collectors.toList());
  }

  @Override
  public Mono<List<String>> getConversationIds() {
    return conversationRepository.getConversationIds().collect(Collectors.toList());
  }

  @Override
  public Mono<ConversationEntity> startConversation(final String messageContent) {
    final ExpressionValue conversationSeed = new ExpressionValue(
        "You are PAL, a chatbot assistant that strives to be as helpful as possible. You prefix every response to a user with the string \"PAL: \".",
        ActorType.INITIAL_PROMPT);
    final ExpressionValue userGreeting = new ExpressionValue(messageContent, USER);
    final ConversationEntity startOfConversation = new ConversationEntity(conversationSeed,
        userGreeting);
    return conversationRepository.create(
            startOfConversation.toRecord())
        .flatMap(conversationRecord -> {
          final ConversationEntity conversation = ConversationEntity.fromRecord(conversationRecord);
          return davinciSingletonService.exchange(conversation)
              .map(conversation::addExpression);
        });
  }

  @Override
  public Mono<ExpressionValue> sendExpression(final String conversationId,
      final String messageContent) {
    return conversationRepository.getConversation(conversationId)
        .switchIfEmpty(Mono.error(new ConversationNotFound(conversationId)))
        .flatMap(conversationRecord -> {
          final ConversationEntity conversation = ConversationEntity.fromRecord(conversationRecord)
              .addExpression(new ExpressionValue(messageContent, USER));
          return davinciSingletonService.exchange(conversation)
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
