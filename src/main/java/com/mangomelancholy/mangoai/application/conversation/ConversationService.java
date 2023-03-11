package com.mangomelancholy.mangoai.application.conversation;

import reactor.core.publisher.Mono;

public interface ConversationService {

  Mono<ConversationEntity> startConversation(String message);

}
