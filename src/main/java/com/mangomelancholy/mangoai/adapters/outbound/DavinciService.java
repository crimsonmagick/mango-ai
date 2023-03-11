package com.mangomelancholy.mangoai.adapters.outbound;

import com.mangomelancholy.mangoai.application.ports.secondary.AIService;
import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class DavinciService implements AIService {



  @Override
  public ExpressionValue exchange(final ConversationEntity conversationEntity) {
    return null;
  }
}
