package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConversationSerializer {

  private final CompletionExpressionSerializer completionExpressionSerializer;

  public ConversationSerializer(final CompletionExpressionSerializer completionExpressionSerializer) {
    this.completionExpressionSerializer = completionExpressionSerializer;
  }

  public String serializeConversation(final ConversationEntity conversation) {
    return conversation.getExpressions().stream()
        .map(completionExpressionSerializer::serializeExpression)
        .collect(Collectors.joining());
  }

}
