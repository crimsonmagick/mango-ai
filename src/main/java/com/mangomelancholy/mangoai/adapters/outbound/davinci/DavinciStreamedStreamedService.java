package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.AIStreamedService;
import com.mangomelancholy.mangoai.infrastructure.completions.OpenAICompletionsClient;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DavinciStreamedStreamedService implements AIStreamedService {

  private static final Logger log = LogManager.getLogger(DavinciStreamedStreamedService.class);
  private final CompletionUtility completionUtility;
  private final OpenAICompletionsClient completionsClient;
  private final ConversationSerializer conversationSerializer;

  public DavinciStreamedStreamedService(final OpenAICompletionsClient completionsClient,
      final ConversationSerializer conversationSerializer, final CompletionUtility completionUtility) {
    this.completionsClient = completionsClient;
    this.conversationSerializer = conversationSerializer;
    this.completionUtility = completionUtility;
  }

  @Override
  public Flux<ExpressionFragment> exchange(final ConversationEntity conversationEntity) {
    final StringBuilder sb = new StringBuilder();
    final AtomicBoolean attributionRemoved = new AtomicBoolean(false);
    return completionsClient.streamed()
        .complete(conversationSerializer.serializeConversation(conversationEntity))
        .handle((textCompletion, sink) -> {
          final String contentFragment = completionUtility.extractChoiceText(textCompletion);
          if (attributionRemoved.get()) {
            sink.next(contentFragment);
          } else {
            sb.append(contentFragment);
            final String bufferedString = sb.toString();
            final int index = completionUtility.lastIndexOfAttribution(bufferedString);
            if (index != -1) {
              attributionRemoved.set(true);
              sink.next(completionUtility.normalizeChoice(bufferedString));
            }
          }
        })
        // in case Davinci gets confused with the prefix.
        .concatWith(Mono.defer(() -> {
          if (!attributionRemoved.get()) {
            return Mono.just(sb.toString());
          } else {
            return Mono.empty();
          }
        }))
        .index()
        .map(tuple -> new ExpressionFragment((String) tuple.getT2(), conversationEntity.getConversationId(), tuple.getT1()));
  }

}
