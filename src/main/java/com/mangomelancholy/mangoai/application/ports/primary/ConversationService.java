package com.mangomelancholy.mangoai.application.ports.primary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import reactor.core.publisher.Mono;

public interface ConversationService {

  Mono<ConversationEntity> startConversation(String messageContent);

  Mono<ExpressionValue> sendExpression(String conversationId, String messageContent);

}
