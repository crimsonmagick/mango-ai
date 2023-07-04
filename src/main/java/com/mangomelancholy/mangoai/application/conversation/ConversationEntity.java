package com.mangomelancholy.mangoai.application.conversation;

import com.mangomelancholy.mangoai.application.conversation.ports.secondary.ConversationRecord;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.ExpressionRecord;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConversationEntity {

  private final String conversationId;
  private final List<ExpressionValue> expressionValues;
  private final String summary;

  private final ZonedDateTime createdAt;
  private final ZonedDateTime updatedAt;

  public ConversationEntity(final ExpressionValue systemSeed, final ExpressionValue userGreeting) {
    assert systemSeed != null;
    assert userGreeting != null && userGreeting.content() != null && !"".equals(userGreeting.content().trim());

    expressionValues = List.of(systemSeed, userGreeting);
    conversationId = null;
    final int length = userGreeting.content().length();
    final int lastIndex = length < 128 ? length : 127;
    summary = userGreeting.content().substring(0, lastIndex);
    createdAt = null;
    updatedAt = null;
  }
  public ConversationEntity(final String conversationId, final List<ExpressionValue> expressionValues, final String summary,
      final ZonedDateTime createdAt, final ZonedDateTime updatedAt) {

    assert conversationId != null;
    assert expressionValues != null;

    this.conversationId = conversationId;
    this.expressionValues = expressionValues;
    this.summary = summary;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public ConversationEntity addExpression(final ExpressionValue newExpression) {
    final List<ExpressionValue> updatedExpressions = new ArrayList<>(expressionValues);
    updatedExpressions.add(newExpression);
    return new ConversationEntity(conversationId, updatedExpressions, summary, createdAt, updatedAt);
  }

  public static ConversationEntity fromRecord(final ConversationRecord conversationRecord) {
    final List<ExpressionValue> expressionValues = conversationRecord.expressions().stream().map(ExpressionValue::fromRecord).toList();
    return new ConversationEntity(conversationRecord.conversationId(), expressionValues, conversationRecord.summary(),
        conversationRecord.createdAt(), conversationRecord.updatedAt());
  }

  public String getConversationId() {
    return conversationId;
  }

  public List<ExpressionValue> getExpressions() {
    return expressionValues;
  }

  public ExpressionValue getLastExpression() {
    return expressionValues.get(expressionValues.size() - 1);
  }

  public String getSummary() {
    return summary;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public ZonedDateTime getUpdatedAt() {
    return updatedAt;
  }

  public ConversationRecord toRecord() {
    final List<ExpressionRecord> expressionRecords = expressionValues.stream().map(ExpressionValue::toRecord).toList();
    return new ConversationRecord(conversationId, expressionRecords, summary, null, null);
  }

}
