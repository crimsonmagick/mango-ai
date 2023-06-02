package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import com.mangomelancholy.mangoai.infrastructure.OpenAICompletionsClient;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DavinciStreamService {

  private static final Logger log = LogManager.getLogger(DavinciStreamService.class);
  private final CompletionMapper completionMapper;
  private final OpenAICompletionsClient completionsClient;
  private final ConversationSerializer conversationSerializer;

  public DavinciStreamService(final OpenAICompletionsClient completionsClient,
      final ConversationSerializer conversationSerializer, final CompletionMapper completionMapper) {
    this.completionsClient = completionsClient;
    this.conversationSerializer = conversationSerializer;
    this.completionMapper = completionMapper;
  }

  public Flux<ExpressionFragment> exchange(final ConversationEntity conversationEntity) {
    final StringBuilder sb = new StringBuilder();
    final AtomicBoolean attributionRemoved = new AtomicBoolean(false);
    return completionsClient.streamed()
        .complete(conversationSerializer.serializeConversation(conversationEntity))
        .handle((textCompletion, sink) -> {
          final String contentFragment = completionMapper.extractFragment(textCompletion);
          if (attributionRemoved.get()) {
            sink.next(contentFragment);
          } else {
            sb.append(contentFragment);
            final String bufferedString = sb.toString();
            final int index = completionMapper.lastIndexOfAttribution(bufferedString);
            if (index != -1) {
              attributionRemoved.set(true);
              sink.next(completionMapper.normalizeChoice(bufferedString));
            }
          }
        })
        .index()
        .map(tuple -> new ExpressionFragment((String) tuple.getT2(), conversationEntity.getConversationId(), tuple.getT1()));
  }

}
