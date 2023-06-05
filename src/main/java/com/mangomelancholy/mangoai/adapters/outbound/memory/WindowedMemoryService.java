package com.mangomelancholy.mangoai.adapters.outbound.memory;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.MemoryService;
import com.mangomelancholy.mangoai.infrastructure.ModelInfoService;
import com.mangomelancholy.mangoai.infrastructure.ModelInfoService.ModelType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class WindowedMemoryService implements MemoryService {

  private final ModelInfoService modelInfoService;
  private final EncodingRegistry registry;
  private final SerializationDelegator serializationDelegator;

  WindowedMemoryService(final ModelInfoService modelInfoService, final SerializationDelegator serializationDelegator) {
    this.modelInfoService = modelInfoService;
    this.serializationDelegator = serializationDelegator;
    this.registry = Encodings.newDefaultEncodingRegistry();
  }


  @Override
  public ConversationEntity rememberConversation(final ConversationEntity conversation, String model) {
    final ModelType modelType = ModelType.fromString(model);
    final long MAX_INPUT_TOKENS = modelInfoService.getMaxInputTokens(modelType);
    final Encoding encoding = registry.getEncoding(modelInfoService.getEncoding(modelType));
    final List<Tuple2<Long, ExpressionValue>> tokenPairs = conversation.getExpressions().stream()
        .map(value -> {
          final String serialized = serializationDelegator.serializeAsString(value, model);
          final long tokenCount = encoding.encode(serialized)
              .size();
          return Tuples.of(tokenCount, value);
        })
        .toList();
    long totalTokens = tokenPairs.stream()
        .map(Tuple2::getT1)
        .reduce(0L, Long::sum);
    final List<ExpressionValue> remembered;
    if (totalTokens > MAX_INPUT_TOKENS) {
      remembered = new ArrayList<>();
      for (final Tuple2<Long, ExpressionValue> pair : tokenPairs) {
        if (totalTokens <= MAX_INPUT_TOKENS || pair.getT2().actor() == ActorType.INITIAL_PROMPT) {
          remembered.add(pair.getT2());
        } else {
          totalTokens = totalTokens - pair.getT1();
        }
      }
    } else {
      remembered = new ArrayList<>(conversation.getExpressions());
    }
    return new ConversationEntity(conversation.getConversationId(), remembered);
  }
}
