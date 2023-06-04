package com.mangomelancholy.mangoai.adapters.outbound.memory;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.ports.secondary.MemoryService;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class WindowedMemoryService implements MemoryService {

  @Override
  public ConversationEntity rememberConversation(final ConversationEntity conversation) {
    return new ConversationEntity(conversation.getConversationId(), new ArrayList<>(conversation.getExpressions()));
  }
}
