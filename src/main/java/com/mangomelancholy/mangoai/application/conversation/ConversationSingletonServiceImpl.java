package com.mangomelancholy.mangoai.application.conversation;

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.USER;

import com.mangomelancholy.mangoai.application.AiServiceResolver;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.conversation.ports.primary.ConversationNotFound;
import com.mangomelancholy.mangoai.application.conversation.ports.primary.ConversationSingletonService;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.AiSingletonService;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.ConversationRepository;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.MemoryService;
import com.mangomelancholy.mangoai.infrastructure.ModelInfoService;
import com.mangomelancholy.mangoai.infrastructure.ModelInfoService.ModelType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
public class ConversationSingletonServiceImpl implements ConversationSingletonService {

  private final AiServiceResolver aiServiceResolver;
  private final ConversationRepository conversationRepository;
  private final MemoryService memoryService;
  private final ModelInfoService modelInfoService;

  @Override
  public Mono<List<String>> getConversationIds() {
    return conversationRepository.getConversationIds().collect(Collectors.toList());
  }

  @Override
  public Mono<ConversationEntity> startConversation(final String messageContent, String model) {
    final AiSingletonService aiSingletonService = aiServiceResolver.resolveSingletonService(model);
    final ExpressionValue conversationSeed = new ExpressionValue(
        modelInfoService.getInitialPrompt(ModelType.CHAT_GPT), ActorType.INITIAL_PROMPT, null);
    final ExpressionValue userGreeting = new ExpressionValue(messageContent, USER, null);
    final ConversationEntity startOfConversation = new ConversationEntity(conversationSeed,
        userGreeting);
    return conversationRepository.create(
            startOfConversation.toRecord())
        .flatMap(conversationRecord -> {
          final ConversationEntity savedConversation = ConversationEntity.fromRecord(
              conversationRecord);
          return aiSingletonService.exchange(savedConversation)
              .flatMap(expressionValue -> conversationRepository.addExpression(
                  expressionValue.toRecord()))
              .map(expressionRecord -> savedConversation.addExpression(
                  ExpressionValue.fromRecord(expressionRecord)));
        });
  }

  @Override
  public Mono<ExpressionValue> sendExpression(final String conversationId,
      final String messageContent, final String model) {
    final AiSingletonService aiSingletonService = aiServiceResolver.resolveSingletonService("gpt3");
    return conversationRepository.getConversation(conversationId)
        .switchIfEmpty(Mono.error(new ConversationNotFound(conversationId)))
        .flatMap(conversationRecord -> {
          final ConversationEntity fullConversation = ConversationEntity.fromRecord(
                  conversationRecord)
              .addExpression(new ExpressionValue(messageContent, USER, conversationId));
          final ConversationEntity rememberedConversation = memoryService.rememberConversation(
              fullConversation, model);
          return aiSingletonService.exchange(rememberedConversation)
              .flatMap(responseExpression -> conversationRepository.addExpression(
                  responseExpression.toRecord()));
        })
        .map(ExpressionValue::fromRecord);
  }

  @Override
  public Mono<List<ExpressionValue>> getExpressions(final String conversationId) {
    return conversationRepository.getExpressions(conversationId)
        .switchIfEmpty(Mono.error(new ConversationNotFound(conversationId)))
        .map(ExpressionValue::fromRecord)
        .collect(Collectors.toList());
  }
}
