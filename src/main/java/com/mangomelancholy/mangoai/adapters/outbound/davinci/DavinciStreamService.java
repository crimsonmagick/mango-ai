package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.infrastructure.OpenAICompletionsClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DavinciStreamService {

  private static final Logger log = LogManager.getLogger(DavinciStreamService.class);
  private final OpenAICompletionsClient completionsClient;
  private final ConversationSerializer conversationSerializer;
  private final CompletionMapper completionMapper;

  public DavinciStreamService(final OpenAICompletionsClient completionsClient,
      final ConversationSerializer conversationSerializer, final CompletionMapper completionMapper) {
    this.completionsClient = completionsClient;
    this.conversationSerializer = conversationSerializer;
    this.completionMapper = completionMapper;
  }


  public Flux<ExpressionFragment> exchange(final ConversationEntity conversationEntity) {
    return completionsClient.streamed()
        .complete(conversationSerializer.serializeConversation(conversationEntity))
        .index()
        .map(tuple -> completionMapper.mapFragment(tuple.getT2(), tuple.getT1()));
  }

}
