package com.mangomelancholy.mangoai.application.ports.primary;

import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import reactor.core.publisher.Flux;

public interface ConversationStreamedService<T extends ExpressionFragment> {

  Flux<T> startConversation(String messageContent);

  Flux<T> sendExpression(String conversationId, String messageContent);

}
