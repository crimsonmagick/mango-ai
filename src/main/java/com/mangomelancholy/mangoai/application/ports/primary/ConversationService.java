package com.mangomelancholy.mangoai.application.ports.primary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import reactor.core.publisher.Mono;

public interface ConversationService {

  Mono<ConversationEntity> startConversation();

}
