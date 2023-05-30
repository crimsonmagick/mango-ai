package com.mangomelancholy.mangoai.application.ports.secondary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface AIService<T extends Publisher<ExpressionValue>> {

  Publisher<ExpressionValue> exchange(ConversationEntity conversationEntity);
}
