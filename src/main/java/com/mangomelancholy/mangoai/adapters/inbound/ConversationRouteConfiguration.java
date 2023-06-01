package com.mangomelancholy.mangoai.adapters.inbound;

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.INITIAL_PROMPT;
import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.mangomelancholy.mangoai.adapters.outbound.davinci.DavinciStreamService;
import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ConversationSingletonServiceImpl;
import com.mangomelancholy.mangoai.application.conversation.ConversationStreamedServiceImpl;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class ConversationRouteConfiguration {

  private static final Logger log = LogManager.getLogger(ConversationRouteConfiguration.class);
  private final ConversationSingletonServiceImpl conversationSingletonService;
  private final ConversationStreamedServiceImpl conversationStreamedService;

  public ConversationRouteConfiguration(final ConversationSingletonServiceImpl conversationSingletonService,
      final ConversationStreamedServiceImpl conversationStreamedService) {
    this.conversationSingletonService = conversationSingletonService;
    this.conversationStreamedService = conversationStreamedService;
  }

  @Bean
  public RouterFunction<ServerResponse> conversationRoutes() {
    return RouterFunctions.route(RequestPredicates.GET("/conversations"),
            request -> ServerResponse.ok().contentType(APPLICATION_JSON)
                .bodyValue(new ExpressionJson(null, "Hello there, I'm PAL! Please start a new conversation.")))
        .andRoute(RequestPredicates.POST("/conversations"), request -> {
          final Mono<ExpressionJson> jsonMono = request.bodyToMono(ExpressionJson.class)
              .flatMap(message -> conversationSingletonService.startConversation(message.content()))
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
              .flatMap(expressionJson -> conversationSingletonService.sendExpression(id, expressionJson.content())
                  .map(expressionValue -> new ExpressionJson(id, expressionValue.content())))
              .doOnError(throwable -> {
                log.error("Error processing request.", throwable);
              });
          return ServerResponse.ok().contentType(APPLICATION_JSON)
              .body(response, ExpressionJson.class);
        })
        .andRoute(RequestPredicates.POST("/streamed/conversations"), request -> {
          final Flux<ExpressionFragment> responseStream = request.bodyToMono(String.class)
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
          return ServerResponse.ok()
              .contentType(MediaType.APPLICATION_NDJSON).body(responseStream, new ParameterizedTypeReference<>() {
              });
        });
  }

  @Bean
  CorsWebFilter corsFilter() {
    return new CorsWebFilter(exchange -> new CorsConfiguration().applyPermitDefaultValues());
  }

}
