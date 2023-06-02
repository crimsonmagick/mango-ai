package com.mangomelancholy.mangoai.application.ports.secondary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import reactor.core.publisher.Flux;

public interface AIStreamedService {

  Flux<ExpressionFragment> exchange(final ConversationEntity conversationEntity);


}
