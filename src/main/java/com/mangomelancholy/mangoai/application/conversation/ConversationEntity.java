package com.mangomelancholy.mangoai.application.conversation;

import com.mangomelancholy.mangoai.application.conversation.repository.ConversationRecord;
import com.mangomelancholy.mangoai.application.conversation.repository.ExpressionRecord;
import java.util.Collections;
import java.util.List;

public class ConversationEntity {

  public ConversationEntity(final ExpressionValue userExpressionValue) {
    assert userExpressionValue != null;

    expressionValues = Collections.singletonList(userExpressionValue);
    conversationId = null;
  }

  private ConversationEntity(final String conversationId, final List<ExpressionValue> expressionValues) {
    assert conversationId != null;
    assert expressionValues != null;

    this.conversationId = conversationId;
    this.expressionValues = expressionValues;
  }

  private final List<ExpressionValue> expressionValues;
  private final String conversationId;


  String getConversationId() {
    return conversationId;
  }
  ExpressionValue getLastExpression() {
    return expressionValues.get(0);
  }

  public ConversationRecord toRecord(final ConversationEntity conversation) {
    final List<ExpressionRecord> expressionRecords = expressionValues.stream().map(ExpressionValue::toRecord).toList();
    return new ConversationRecord(conversationId, expressionRecords);
  }

  public static ConversationEntity fromRecord(final ConversationRecord conversationRecord) {
    final List<ExpressionValue> expressionValues = conversationRecord.expressions().stream().map(ExpressionValue::fromRecord).toList();
    return new ConversationEntity(conversationRecord.conversationId(), expressionValues);
  }

}
