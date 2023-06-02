package com.mangomelancholy.mangoai.application.conversation;

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.PAL;
import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.USER;

import com.mangomelancholy.mangoai.adapters.outbound.davinci.CompletionUtility;
import com.mangomelancholy.mangoai.adapters.outbound.davinci.DavinciStreamService;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.ports.secondary.ConversationRepository;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ConversationStreamedServiceImpl {
  private static final Logger log = LogManager.getLogger(ConversationStreamedServiceImpl.class);

  private final CompletionUtility completionUtility;
  private final ConversationRepository conversationRepository;
  private final DavinciStreamService davinciStreamService;

  public ConversationStreamedServiceImpl(final ConversationRepository conversationRepository,
      final DavinciStreamService davinciStreamService, final CompletionUtility completionUtility) {
    this.conversationRepository = conversationRepository;
    this.davinciStreamService = davinciStreamService;
    this.completionUtility = completionUtility;
  }

  public Flux<ExpressionFragment> startConversation(final String messageContent) {
    final ExpressionValue conversationSeed = new ExpressionValue(
        "You are PAL, a chatbot assistant that strives to be as helpful as possible. You prefix every response to a user with the string \"PAL: \".",
        ActorType.INITIAL_PROMPT);
    final ExpressionValue palGreeting = new ExpressionValue(messageContent, USER);
    final ConversationEntity startOfConversation = new ConversationEntity(conversationSeed,
        palGreeting);
    final Flux<ExpressionFragment> fragmentStream = conversationRepository.create(startOfConversation.toRecord())
        .flatMapMany(conversationRecord -> {
          final ConversationEntity conversation = ConversationEntity.fromRecord(conversationRecord);
          return davinciStreamService.exchange(conversation);
        });
    fragmentStream.map(ExpressionFragment::contentFragment)
        .collect(Collectors.joining())
        .map(content -> {
          final String normalizedContent = completionUtility.normalizeChoice(content);
          return startOfConversation.addExpression(new ExpressionValue(normalizedContent, PAL));
        })
        .doOnNext(updatedConversation -> conversationRepository.update(updatedConversation.toRecord()))
        .doOnError(throwable -> log.info("Error updating conversation with PAL response.", throwable))
        .subscribe();
    return fragmentStream;
  }

}
