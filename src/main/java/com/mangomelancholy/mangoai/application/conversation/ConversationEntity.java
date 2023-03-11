package com.mangomelancholy.mangoai.application.conversation;

import com.mangomelancholy.mangoai.application.conversation.repository.ConversationRecord;
import com.mangomelancholy.mangoai.application.conversation.repository.ExpressionRecord;
import java.util.Collections;
import java.util.List;

public class ConversationEntity {

  public ConversationEntity(final Expression userExpression) {
    assert userExpression != null;

    expressions = Collections.singletonList(userExpression);
    conversationId = null;
  }

  private ConversationEntity(final String conversationId, final List<Expression> expressions) {
    assert conversationId != null;
    assert expressions != null;

    this.conversationId = conversationId;
    this.expressions = expressions;
  }

  private final List<Expression> expressions;
  private final String conversationId;


  String getConversationId() {
    return conversationId;
  }
  Expression getLastExpression() {
    return expressions.get(0);
  }

  public ConversationRecord toRecord(final ConversationEntity conversation) {
    final List<ExpressionRecord> expressionRecords = expressions.stream().map(Expression::toRecord).toList();
    return new ConversationRecord(conversationId, expressionRecords);
  }

  public static ConversationEntity fromRecord(final ConversationRecord conversationRecord) {
    final List<Expression> expressions = conversationRecord.expressions().stream().map(Expression::fromRecord).toList();
    return new ConversationEntity(conversationRecord.conversationId(), expressions);
  }

}
