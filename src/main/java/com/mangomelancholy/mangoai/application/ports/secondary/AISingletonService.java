package com.mangomelancholy.mangoai.application.ports.secondary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import reactor.core.publisher.Mono;

public interface AISingletonService extends AIService {
    @Override
    Mono<ExpressionValue> exchange(ConversationEntity conversationEntity);
}
