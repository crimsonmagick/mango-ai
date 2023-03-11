package com.mangomelancholy.mangoai.application.conversation;

import com.mangomelancholy.mangoai.application.conversation.repository.ExpressionRecord;

public record ExpressionValue(String content, ActorType actor) {

  public enum ActorType {
    USER, SYSTEM, PAL
  }

  public ExpressionValue {
    assert content != null;
    assert actor != null;
  }

  public static ExpressionValue fromRecord(final ExpressionRecord expressionRecord) {
    return new ExpressionValue(expressionRecord.content(), ActorType.valueOf(expressionRecord.actor()));
  }

  public ExpressionRecord toRecord() {
    return new ExpressionRecord(content, actor.toString());
  }
}
