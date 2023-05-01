package com.mangomelancholy.mangoai.adapters.inbound;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.mangomelancholy.mangoai.application.ports.primary.ConversationService;
import com.mangomelancholy.mangoai.infrastructure.OpenAICompletionsStreamingClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class ConversationRoutes {

  private static final Logger log = LogManager.getLogger(ConversationRoutes.class);
  private final ConversationService conversationService;
  private final OpenAICompletionsStreamingClient streamingClient;

  public ConversationRoutes(final ConversationService conversationService, final OpenAICompletionsStreamingClient streamingClient) {
    this.conversationService = conversationService;
    this.streamingClient = streamingClient;
  }

  @Bean
  public RouterFunction<ServerResponse> getConversations() {
    return RouterFunctions.route(RequestPredicates.GET("/conversations"),
            request -> ServerResponse.ok().contentType(APPLICATION_JSON)
                .bodyValue(new ExpressionJson(null, "Hello there, I'm PAL! Please start a new conversation.")))
        .andRoute(RequestPredicates.POST("/conversations"), request -> {
          final Mono<ExpressionJson> jsonMono = conversationService.startConversation()
              .map(conversation ->
                  new ExpressionJson(conversation.getConversationId(), conversation.getLastExpression().content()))
              .doOnError(throwable -> {
                log.error("Error processing request.", throwable);
              });
          return ServerResponse.ok().contentType(APPLICATION_JSON)
              .body(jsonMono, ExpressionJson.class);
        })
        .andRoute(RequestPredicates.POST("/conversations/{id}/expressions"), request -> {
          final String id = request.pathVariable("id");
          final Mono<ExpressionJson> response = request.bodyToMono(ExpressionJson.class)
              .flatMap(expressionJson -> conversationService.sendExpression(id, expressionJson.content())
                  .map(expressionValue -> new ExpressionJson(id, expressionValue.content())))
              .doOnError(throwable -> {
                log.error("Error processing request.", throwable);
              });
          return ServerResponse.ok().contentType(APPLICATION_JSON)
              .body(response, ExpressionJson.class);
        })

        .andRoute(RequestPredicates.POST("/streaming/conversations/expressions"), request -> {
          final Flux<String> responseStream = request.bodyToMono(ExpressionJson.class)
              .flatMapMany(expressionJson -> streamingClient.complete(expressionJson.content()))
              .doOnError(throwable -> {
                log.error("Error processing request.", throwable);
              });
          return ServerResponse.ok()
              .contentType(MediaType.TEXT_EVENT_STREAM).body(responseStream, String.class);
        });
  }
}
