package com.mangomelancholy.mangoai.adapters.inbound;

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.INITIAL_PROMPT;
import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.mangomelancholy.mangoai.adapters.outbound.davinci.DavinciStreamService;
import com.mangomelancholy.mangoai.application.conversation.ConversationEntity;
import com.mangomelancholy.mangoai.application.conversation.ConversationSingletonServiceImpl;
import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.infrastructure.OpenAICompletionsClient;
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
  private final ConversationSingletonServiceImpl conversationService;
  private final DavinciStreamService davinciStreamService;

  public ConversationRouteConfiguration(final ConversationSingletonServiceImpl conversationService, final OpenAICompletionsClient streamingClient, final
      DavinciStreamService davinciStreamService) {
    this.conversationService = conversationService;
    this.davinciStreamService = davinciStreamService;
  }

  @Bean
  public RouterFunction<ServerResponse> conversationRoutes() {
    return RouterFunctions.route(RequestPredicates.GET("/conversations"),
            request -> ServerResponse.ok().contentType(APPLICATION_JSON)
                .bodyValue(new ExpressionJson(null, "Hello there, I'm PAL! Please start a new conversation.")))
        .andRoute(RequestPredicates.POST("/conversations"), request -> {
          final Mono<ExpressionJson> jsonMono = request.bodyToMono(ExpressionJson.class)
              .flatMap(message -> conversationService.startConversation(message.content()))
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
          final Flux<ExpressionFragment> responseStream = request.bodyToMono(String.class)
              .flatMapMany(prompt -> {
                final ExpressionValue seedExpression = new ExpressionValue("You are PAL, a chatbot assistant that strives to be as helpful as possible. You prefix every response to a user with the string \"PAL: \".", INITIAL_PROMPT);
                final ExpressionValue promptExpression = new ExpressionValue(prompt, USER);
                final ConversationEntity conversation = new ConversationEntity(seedExpression, promptExpression);
                return davinciStreamService.exchange(conversation);
              })
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
