package com.mangomelancholy.mangoai.application.ports.primary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import org.reactivestreams.Publisher;

public interface ConversationSingletonService<T extends Publisher<ConversationEntity>, U extends Publisher<ExpressionValue>> {

  T startConversation(String messageContent);

  U sendExpression(String conversationId, String messageContent);

}
