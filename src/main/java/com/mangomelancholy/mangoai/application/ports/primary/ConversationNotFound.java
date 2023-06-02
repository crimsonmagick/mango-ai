package com.mangomelancholy.mangoai.application.ports.primary;

public class ConversationNotFound extends RuntimeException {

  public ConversationNotFound(final String conversationId) {
    super();
    this.conversationId = conversationId;
  }

  private String conversationId;

  public String getConversationId() {
    return conversationId;
  }
}
