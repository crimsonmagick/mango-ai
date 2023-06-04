package com.mangomelancholy.mangoai.application.conversation;

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.PAL;
import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.USER;

import com.mangomelancholy.mangoai.adapters.outbound.davinci.DavinciStreamedStreamedService;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.ports.primary.ConversationNotFound;
import com.mangomelancholy.mangoai.application.ports.primary.ConversationStreamedService;
import com.mangomelancholy.mangoai.application.ports.secondary.ConversationRepository;
import com.mangomelancholy.mangoai.application.ports.secondary.MemoryService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ConversationStreamedServiceImpl implements ConversationStreamedService<ExpressionFragment> {

  private static final Logger log = LogManager.getLogger(ConversationStreamedServiceImpl.class);

  private final ConversationRepository conversationRepository;
  @Value("${seeds.davinci.conversation}")
  private final String davinciSeed;
  private final DavinciStreamedStreamedService davinciStreamedService;
  private final MemoryService memoryService;

  @Override
  public Flux<ExpressionFragment> startConversation(final String messageContent) {
    final ExpressionValue conversationSeed = new ExpressionValue(
        davinciSeed,
        ActorType.INITIAL_PROMPT);
    final ExpressionValue userGreeting = new ExpressionValue(messageContent, USER);
    final ConversationEntity startOfConversation = new ConversationEntity(conversationSeed,
        userGreeting);
    return conversationRepository.create(startOfConversation.toRecord())
        .flatMapMany(conversationRecord -> {
          final ConversationEntity conversation = ConversationEntity.fromRecord(conversationRecord);
          Flux<ExpressionFragment> fragmentStream = davinciStreamedService.exchange(conversation)
              .publish()
              .autoConnect(2);
          fragmentStream.map(ExpressionFragment::contentFragment)
              .collect(Collectors.joining())
              .map(content -> conversation.addExpression(new ExpressionValue(content, PAL)))
              .doOnNext(updatedConversation -> conversationRepository.update(updatedConversation.toRecord()))
              .doOnError(throwable -> log.info("Error updating conversation with PAL response.", throwable))
              .subscribe();
          return fragmentStream;
        });
  }

  @Override
  public Flux<ExpressionFragment> sendExpression(final String conversationId, final String messageContent) {
    return conversationRepository.getConversation(conversationId)
        .switchIfEmpty(Mono.error(new ConversationNotFound(conversationId)))
        .map(ConversationEntity::fromRecord)
        .flatMapMany(retrievedConversation -> {
          final ConversationEntity fullConversation = retrievedConversation
              .addExpression(new ExpressionValue(messageContent, USER));
          final ConversationEntity rememberedConversation = memoryService.rememberConversation(fullConversation);
          final Flux<ExpressionFragment> fragmentStream = davinciStreamedService.exchange(rememberedConversation)
              .publish()
              .autoConnect(2);
          fragmentStream.map(ExpressionFragment::contentFragment)
              .collect(Collectors.joining())
              .map(content -> fullConversation.addExpression(new ExpressionValue(content, PAL)))
              .doOnNext(updatedConversation -> conversationRepository.update(updatedConversation.toRecord()))
              .doOnError(throwable -> log.info("Error updating conversation with PAL response.", throwable))
              .subscribe();
          return fragmentStream;
        });
  }

}
