package com.mangomelancholy.mangoai.application.conversation;

import com.mangomelancholy.mangoai.application.conversation.repository.ExpressionRecord;

public class Expression {

  final String actor;
  final String content;
  final String conversationId;
  final String expressionId;

  Expression(final String content, final String actor) {
    assert content != null;
    assert actor != null;

    this.expressionId = null;
    this.conversationId = null;
    this.content = content;
    this.actor = actor;
  }

  private Expression(final String expressionId, final String conversationId, final String content, final String actor) {
    assert expressionId != null;
    assert conversationId!= null;
    assert content!= null;
    assert actor!= null;

    this.expressionId = expressionId;
    this.conversationId = conversationId;
    this.content = content;
    this.actor = actor;
  }

  public static Expression fromRecord(final ExpressionRecord expressionRecord) {
    return new Expression(expressionRecord.expressionId(), expressionRecord.conversationId(), expressionRecord.content(), expressionRecord.actor());
  }

  public ExpressionRecord toRecord() {
    return new ExpressionRecord(expressionId, content, actor, conversationId);
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

  public String getExpressionId() {
    return expressionId;
  }
}
