package com.mangomelancholy.mangoai.adapters.outbound.memory;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.mangomelancholy.mangoai.adapters.outbound.davinci.CompletionExpressionSerializer;
import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.MemoryService;
import com.mangomelancholy.mangoai.infrastructure.ModelRegistry;
import com.mangomelancholy.mangoai.infrastructure.ModelRegistry.ModelType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class WindowedMemoryService implements MemoryService {

  private final ModelRegistry modelRegistry;
  private final EncodingRegistry registry;
  private final SerializationDelegator serializationDelegator;

  WindowedMemoryService(final ModelRegistry modelRegistry, final SerializationDelegator serializationDelegator) {
    this.modelRegistry = modelRegistry;
    this.serializationDelegator = serializationDelegator;
    this.registry = Encodings.newDefaultEncodingRegistry();
  }


  @Override
  public ConversationEntity rememberConversation(final ConversationEntity conversation, String model) {
    final long MAX_INPUT_TOKENS = modelRegistry.getMaxInputTokens(ModelType.DAVINCI);
    final Encoding encoding = registry.getEncoding(EncodingType.R50K_BASE);
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
