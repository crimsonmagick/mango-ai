package com.mangomelancholy.mangoai.application.ports.secondary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;

public interface MemoryService {

  ConversationEntity rememberConversation(ConversationEntity conversation);

}
