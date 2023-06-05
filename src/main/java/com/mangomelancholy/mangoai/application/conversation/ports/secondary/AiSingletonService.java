package com.mangomelancholy.mangoai.application.conversation.ports.secondary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import reactor.core.publisher.Mono;

public interface AiSingletonService {

  Mono<ExpressionValue> exchange(final ConversationEntity conversationEntity);

}
