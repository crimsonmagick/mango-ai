package com.mangomelancholy.mangoai.application.ports.primary;

import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;

public interface ConversationStreamedService<T extends ExpressionFragment> {

  T startConversation(String messageContent);

  T sendExpression(String conversationId, String messageContent);

}
