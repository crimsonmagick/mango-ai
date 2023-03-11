package com.mangomelancholy.mangoai.application.conversation;

import com.mangomelancholy.mangoai.application.conversation.repository.ExpressionRecord;

public class ExpressionValue {

  final String actor;
  final String content;
  final String conversationId;
  final Integer sequenceNumber;

  ExpressionValue(final String content, final String actor) {
    assert content != null;
    assert actor != null;

    this.sequenceNumber = null;
    this.conversationId = null;
    this.content = content;
    this.actor = actor;
  }

  private ExpressionValue(final Integer sequenceNumber, final String conversationId, final String content, final String actor) {
    assert sequenceNumber != null;
    assert conversationId!= null;
    assert content!= null;
    assert actor!= null;

    this.sequenceNumber = sequenceNumber;
    this.conversationId = conversationId;
    this.content = content;
    this.actor = actor;
  }

  public static ExpressionValue fromRecord(final ExpressionRecord expressionRecord) {
    return new ExpressionValue(expressionRecord.sequenceNumber(), expressionRecord.conversationId(), expressionRecord.content(), expressionRecord.actor());
  }

  public ExpressionRecord toRecord() {
    return new ExpressionRecord(sequenceNumber, content, actor, conversationId);
  }

  public String getActor() {
    return actor;
  }

  public String getContent() {
    return content;
  }

  public String getConversationId() {
    return conversationId;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }
}
