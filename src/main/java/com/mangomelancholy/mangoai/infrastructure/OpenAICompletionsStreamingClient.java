package com.mangomelancholy.mangoai.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangomelancholy.mangoai.adapters.inbound.ConversationRouteConfiguration;
import com.mangomelancholy.mangoai.application.ports.secondary.TextCompletion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class OpenAICompletionsStreamingClient {

  private static final Logger log = LogManager.getLogger(OpenAICompletionsStreamingClient.class);

  private final String apiKey;
  private final ObjectMapper objectMapper;
  private final WebClient webClient;

  public OpenAICompletionsStreamingClient(@Value("${pal.secrets.authkey}") final String apiKey, final ObjectMapper objectMapper) {
    this.webClient = WebClient.create("https://api.openai.com/v1/");
    this.apiKey = apiKey;
    this.objectMapper = objectMapper;
  }

  public Flux<TextCompletion> complete(final String prompt) {
    final OpenAIRequest request = new OpenAIRequest.Builder()
        .model("text-davinci-003")
        .prompt(prompt)
        .temperature(0.5)
        .maxTokens(300)
        .topP(0.3)
        .frequencyPenalty(0.5)
        .presencePenalty(0)
        .stream(true)
        .build();

    return webClient.post()
        .uri("completions")
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + apiKey)
        .body(BodyInserters.fromValue(request))
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
        })
        .filter(event -> !"[DONE]".equals(event.data()))
        .map(event -> {
          try {
            return objectMapper.readValue(event.data(), TextCompletion.class);
          } catch (final JsonProcessingException e) {
            throw new RuntimeException(String.format("Unable to parse data value. data=%s", event.data()), e);
          }
        })
        .onErrorContinue((throwable, event) -> log.warn("Error processing event. event={}", event, throwable));
  }

}
