package com.mangomelancholy.mangoai.application.conversation.repository;

public interface ConversationRepository {
  ConversationRecord create(ConversationRecord newConversation);
  ConversationRecord getConversation(ConversationRecord conversation);
  void update(ConversationRecord conversation);

}
