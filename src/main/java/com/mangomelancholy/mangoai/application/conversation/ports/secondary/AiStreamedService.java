package com.mangomelancholy.mangoai.application.conversation.ports.secondary;

import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import reactor.core.publisher.Flux;

public interface AiStreamedService {

  Flux<ExpressionFragment> exchange(final ConversationEntity conversationEntity);

}
