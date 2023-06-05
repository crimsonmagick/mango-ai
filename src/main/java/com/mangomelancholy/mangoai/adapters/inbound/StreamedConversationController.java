package com.mangomelancholy.mangoai.adapters.inbound;

import com.mangomelancholy.mangoai.application.conversation.ConversationStreamedServiceImpl;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class StreamedConversationController {

  private static final Logger log = LogManager.getLogger(StreamedConversationController.class);
  private final ConversationStreamedServiceImpl conversationStreamedService;

  public StreamedConversationController(final ConversationStreamedServiceImpl conversationStreamedService) {
    this.conversationStreamedService = conversationStreamedService;
  }

  @PostMapping(value = "/streamed/conversations/{id}/expressions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
  public Flux<ExpressionFragment> sendExpression(@PathVariable String id, @RequestBody ExpressionJson expressionJson) {
    final String model = expressionJson.model() == null ? "gpt3" : expressionJson.model();
    return conversationStreamedService.sendExpression(id, expressionJson.content(), model)
        .doOnError(throwable -> {
          log.error("Error processing request.", throwable);
        });
  }

  @PostMapping(value = "/streamed/conversations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
  public Flux<ExpressionFragment> startStreamedConversation(final @RequestBody ExpressionJson expressionJson) {
    final String model = expressionJson.model() == null ? "gpt3" : expressionJson.model();
    return conversationStreamedService.startConversation(expressionJson.content(), model)
        .doOnError(throwable -> {
          log.error("Error processing request.", throwable);
        });
  }

}
