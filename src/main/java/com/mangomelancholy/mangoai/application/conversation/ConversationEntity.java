package com.mangomelancholy.mangoai.application.conversation;

import com.mangomelancholy.mangoai.application.conversation.ports.secondary.ConversationRecord;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.ExpressionRecord;
import java.util.ArrayList;
import java.util.List;

public class ConversationEntity {

  public ConversationEntity(final ExpressionValue systemSeed, final ExpressionValue palGreeting) {
    assert systemSeed != null;
    assert palGreeting != null;

    expressionValues = List.of(systemSeed, palGreeting);
    conversationId = null;
  }

  public ConversationEntity(final String conversationId, final List<ExpressionValue> expressionValues) {
    assert conversationId != null;
    assert expressionValues != null;

    this.conversationId = conversationId;
    this.expressionValues = expressionValues;
  }

  private final List<ExpressionValue> expressionValues;
  private final String conversationId;

  public ConversationRecord toRecord() {
    final List<ExpressionRecord> expressionRecords = expressionValues.stream().map(ExpressionValue::toRecord).toList();
    return new ConversationRecord(conversationId, expressionRecords);
  }

  public static ConversationEntity fromRecord(final ConversationRecord conversationRecord) {
    final List<ExpressionValue> expressionValues = conversationRecord.expressions().stream().map(ExpressionValue::fromRecord).toList();
    return new ConversationEntity(conversationRecord.conversationId(), expressionValues);
  }

  public String getConversationId() {
    return conversationId;
  }

  public ConversationEntity addExpression(final ExpressionValue newExpression) {
    final List<ExpressionValue> updatedExpressions = new ArrayList<>(expressionValues);
    updatedExpressions.add(newExpression);
    return new ConversationEntity(conversationId, updatedExpressions);
  }
  public ExpressionValue getLastExpression() {
    return expressionValues.get(expressionValues.size() - 1);
  }

  public List<ExpressionValue> getExpressions() {
    return expressionValues;
  }

}
