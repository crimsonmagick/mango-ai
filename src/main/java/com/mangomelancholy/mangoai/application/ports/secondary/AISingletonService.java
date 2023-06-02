package com.mangomelancholy.mangoai.application.ports.secondary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AISingletonService {

  Mono<ExpressionValue> exchange(final ConversationEntity conversationEntity);


}
