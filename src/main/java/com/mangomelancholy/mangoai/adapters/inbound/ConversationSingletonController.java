package com.mangomelancholy.mangoai.adapters.inbound;

import com.mangomelancholy.mangoai.application.conversation.ConversationSingletonSingletonServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ConversationSingletonController {

  private static final Logger log = LogManager.getLogger(ConversationSingletonController.class);
  private final ConversationSingletonSingletonServiceImpl conversationSingletonService;

  public ConversationSingletonController(final ConversationSingletonSingletonServiceImpl conversationSingletonService) {
    this.conversationSingletonService = conversationSingletonService;
  }

  @GetMapping("/singleton/conversations/ids")
  public Mono<List<String>> getConversations() {
    return conversationSingletonService.getConversationIds();
  }

  @GetMapping("/singleton/conversations/{id}/expressions")
  public Mono<List<ExpressionJson>> getExpressions(@PathVariable String id) {
    return conversationSingletonService.getExpressions(id).map(values -> values.stream()
        .map(value -> new ExpressionJson(null, value.content()))
        .collect(Collectors.toList())
    );
  }

  @PostMapping("/singleton/conversations/{id}/expressions")
  public Mono<ExpressionJson> sendExpression(@PathVariable String id, @RequestBody ExpressionJson expressionJson) {
    return conversationSingletonService.sendExpression(id, expressionJson.content())
        .map(expressionValue -> new ExpressionJson(id, expressionValue.content()))
        .doOnError(throwable -> {
          log.error("Error processing request.", throwable);
        });
  }

  @PostMapping("/singleton/conversations")
  public Mono<ExpressionJson> startConversation(@RequestBody ExpressionJson message) {
    return conversationSingletonService.startConversation(message.content())
        .map(conversation -> new ExpressionJson(conversation.getConversationId(),
            conversation.getLastExpression().content()))
        .doOnError(throwable -> {
          log.error("Error processing request.", throwable);
        });
  }
}