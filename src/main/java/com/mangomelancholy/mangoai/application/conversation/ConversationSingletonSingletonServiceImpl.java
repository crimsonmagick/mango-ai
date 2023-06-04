package com.mangomelancholy.mangoai.application.conversation;

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.USER;

import com.mangomelancholy.mangoai.adapters.outbound.davinci.DavinciSingletonService;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.ports.primary.ConversationNotFound;
import com.mangomelancholy.mangoai.application.ports.primary.ConversationSingletonService;
import com.mangomelancholy.mangoai.application.ports.secondary.ConversationRepository;
import com.mangomelancholy.mangoai.application.ports.secondary.MemoryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
public class ConversationSingletonSingletonServiceImpl implements ConversationSingletonService {

  private final ConversationRepository conversationRepository;
  private final DavinciSingletonService davinciSingletonService;
  private final MemoryService memoryService;
  @Value("${seeds.davinci.conversation}")
  private final String davinciSeed;

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
       davinciSeed, ActorType.INITIAL_PROMPT);
    final ExpressionValue userGreeting = new ExpressionValue(messageContent, USER);
    final ConversationEntity startOfConversation = new ConversationEntity(conversationSeed,
        userGreeting);
    return conversationRepository.create(
            startOfConversation.toRecord())
        .flatMap(conversationRecord -> {
          final ConversationEntity savedConversation = ConversationEntity.fromRecord(conversationRecord);
          return davinciSingletonService.exchange(savedConversation)
              .map(savedConversation::addExpression)
              .doOnNext(conversation -> conversationRepository.update(conversation.toRecord()));
        });
  }

  @Override
  public Mono<ExpressionValue> sendExpression(final String conversationId,
      final String messageContent) {
    return conversationRepository.getConversation(conversationId)
        .switchIfEmpty(Mono.error(new ConversationNotFound(conversationId)))
        .flatMap(conversationRecord -> {
          final ConversationEntity fullConversation = ConversationEntity.fromRecord(conversationRecord)
              .addExpression(new ExpressionValue(messageContent, USER));
          final ConversationEntity rememberedConversation = memoryService.rememberConversation(fullConversation);
          return davinciSingletonService.exchange(rememberedConversation)
              .flatMap(responseExpression -> {
                final ConversationEntity updatedConversation = fullConversation.addExpression(
                    responseExpression);
                return conversationRepository
                    .update(updatedConversation.toRecord());
              });
        })
        .map(conversationRecord -> ConversationEntity.fromRecord(conversationRecord)
            .getLastExpression());
  }
}
