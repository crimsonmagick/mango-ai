package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConversationSerializer {

  private final ExpressionSerializer expressionSerializer;

  public ConversationSerializer(final ExpressionSerializer expressionSerializer) {
    this.expressionSerializer = expressionSerializer;
  }

  public String serializeConversation(final ConversationEntity conversation) {
    return conversation.getExpressions().stream()
        .map(expressionSerializer::serializeExpression)
        .collect(Collectors.joining("\n"));
  }

}
