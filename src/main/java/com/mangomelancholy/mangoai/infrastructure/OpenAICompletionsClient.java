package com.mangomelancholy.mangoai.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class OpenAICompletionsClient {

  private static final Logger log = LogManager.getLogger(OpenAICompletionsClient.class);


  private final String apiKey;
  private final ObjectMapper objectMapper;
  private final WebClient webClient;

  public OpenAICompletionsClient(@Value("${pal.secrets.authkey}") final String apiKey,
      final ObjectMapper objectMapper) {
    this.apiKey = apiKey;
    this.objectMapper = objectMapper;
    this.webClient = WebClient.create("https://api.openai.com/v1/");
  }

  public interface Delegation<T extends Publisher<TextCompletion>> {

    T complete(String prompt);
  }

  public class SingletonDelegation implements Delegation<Mono<TextCompletion>> {

    public Mono<TextCompletion> complete(final String prompt) {
      return OpenAICompletionsClient.this.complete(prompt, false)
          .bodyToMono(TextCompletion.class);
    }
  }

  public class StreamedDelegation implements Delegation<Flux<TextCompletion>> {

    public Flux<TextCompletion> complete(final String prompt) {
      return OpenAICompletionsClient.this.complete(prompt, true)
          .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
          })
          .filter(event -> !"[DONE]".equals(event.data()))
          .map(event -> {
            try {
              return objectMapper.readValue(event.data(), TextCompletion.class);
            } catch (final JsonProcessingException e) {
              throw new RuntimeException(
                  String.format("Unable to parse data value. data=%s", event.data()), e);
            }
          })
          .onErrorContinue(
              (throwable, event) -> log.warn("Error processing event. event={}", event, throwable));
    }
  }

  public SingletonDelegation singleton() {
    return new SingletonDelegation();
  }

  public StreamedDelegation streamed() {
    return new StreamedDelegation();
  }

  private ResponseSpec complete(final String prompt, final boolean stream) {
    final OpenAIRequest request = new OpenAIRequest.Builder()
        .model("text-davinci-003")
        .prompt(prompt)
        .temperature(0.5)
        .maxTokens(300)
        .topP(0.3)
        .frequencyPenalty(0.5)
        .presencePenalty(0)
        .stream(stream)
        .build();

    return webClient.post()
        .uri("completions")
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + apiKey)
        .body(BodyInserters.fromValue(request))
        .retrieve();
  }

}
