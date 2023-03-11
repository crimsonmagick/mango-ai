package com.mangomelancholy.mangoai.application.ports.secondary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;

public interface AIService {
  ExpressionValue exchange(ConversationEntity conversationEntity);
}
