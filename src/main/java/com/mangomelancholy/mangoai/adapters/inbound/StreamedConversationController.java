package com.mangomelancholy.mangoai.adapters.inbound;

import com.mangomelancholy.mangoai.application.conversation.ConversationStreamedServiceImpl;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;

@RestController
public class StreamedConversationController {

  private static final Logger log = LogManager.getLogger(StreamedConversationController.class);
  private final ConversationStreamedServiceImpl conversationStreamedService;

  public StreamedConversationController(final ConversationStreamedServiceImpl conversationStreamedService) {
    this.conversationStreamedService = conversationStreamedService;
  }

  @PostMapping(value = "/streamed/conversations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
  public Flux<ExpressionFragment> startStreamedConversation(final ServerRequest request) {
    return request.bodyToMono(String.class)
        .flatMapMany(conversationStreamedService::startConversation)
        .doOnNext(event -> {
          if (event == null) {
            log.warn("Received null response from server.");
          }
        })
        .filter(Objects::nonNull)
        .doOnError(throwable -> {
          log.error("Error processing request.", throwable);
        });
  }

}
