package com.mangomelancholy.mangoai.adapters.inbound;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class ConversationRoutes {

  private static final Logger log = LogManager.getLogger(ConversationRoutes.class);

  @Bean
  public RouterFunction<ServerResponse> getConversations() {
    return RouterFunctions.route(RequestPredicates.GET("/conversations"),
        request -> ServerResponse.ok().contentType(APPLICATION_JSON)
          .bodyValue(new ExpressionJson("Hello there, I'm PAL! Please start a new conversation.")))
      .andRoute(RequestPredicates.POST("/conversations"), request -> {
        final Mono<ExpressionJson> response = request.bodyToMono(ExpressionJson.class)
          .map(expressionJson -> {
            final String message = String.format(
              "Thanks! I've created a new conversation. Here's the expression I received from you: %s",
              expressionJson.content());
            return new ExpressionJson(message);
          })
          .doOnError(throwable -> {
            log.error("Error processing request.", throwable);
          });
        return ServerResponse.ok().contentType(APPLICATION_JSON)
          .body(response, ExpressionJson.class);
      });
  }
}
