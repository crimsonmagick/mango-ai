package com.mangomelancholy.mangoai.adapters.inbound;

import com.mangomelancholy.mangoai.application.conversation.ConversationSingletonSingletonServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ConversationController {

  private static final Logger log = LogManager.getLogger(ConversationController.class);
  private final ConversationSingletonSingletonServiceImpl conversationSingletonService;

  public ConversationController(final ConversationSingletonSingletonServiceImpl conversationSingletonService) {
    this.conversationSingletonService = conversationSingletonService;
  }

  @GetMapping("/singleton/conversations")
  public Mono<ExpressionJson> getConversations() {
    return Mono.just(new ExpressionJson(null, "Hello there, I'm PAL! Please start a new conversation."));
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

  @PostMapping("/singleton/conversations/{id}/expressions")
  public Mono<ExpressionJson> sendExpression(@PathVariable String id, @RequestBody ExpressionJson expressionJson) {
    return conversationSingletonService.sendExpression(id, expressionJson.content())
        .map(expressionValue -> new ExpressionJson(id, expressionValue.content()))
        .doOnError(throwable -> {
          log.error("Error processing request.", throwable);
        });
  }
}
