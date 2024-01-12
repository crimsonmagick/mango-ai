package com.mangomelancholy.mangoai.adapters.outbound.llama;

import com.mangomelancholy.mangoai.adapters.outbound.chat.ChatExpressionMapper;
import com.mangomelancholy.mangoai.adapters.outbound.chat.ChatUtility;
import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.AiStreamedService;
import com.mangomelancholy.mangoai.infrastructure.ModelInfoService.ModelType;
import com.mangomelancholy.mangoai.infrastructure.chat.OpenAIChatClient;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LlamaStreamedService implements AiStreamedService {

  @Override
  public Flux<ExpressionFragment> exchange(final ConversationEntity conversationEntity, ModelType modelType) {
    return Flux.empty();
  }
}
