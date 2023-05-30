package com.mangomelancholy.mangoai.application.ports.secondary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import org.reactivestreams.Publisher;

public interface AIService {
  Publisher<ExpressionValue> exchange(ConversationEntity conversationEntity);
}
