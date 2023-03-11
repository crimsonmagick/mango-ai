package com.mangomelancholy.mangoai.application.conversation;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ConversationEntity {

  public ConversationEntity(final Expression userExpression) {
    expressions = Collections.singletonList(userExpression);
    conversationId = UUID.randomUUID().toString();
  }

  private final List<Expression> expressions;
  private final String conversationId;

  Expression getLastExpression() {
    return expressions.get(0);
  }

}
