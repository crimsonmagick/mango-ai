package com.mangomelancholy.mangoai.application.conversation;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import reactor.core.publisher.Mono;

public interface ConversationService {

  Mono<ConversationEntity> createConversation(String message);

}
