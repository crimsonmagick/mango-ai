package com.mangomelancholy.mangoai.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OpenAICompletionsClient {

    public OpenAICompletionsClient(@Value("${pal.secrets.authkey}") final String apiKey) {
        this.webClient = WebClient.create("https://api.openai.com/v1/");
        this.apiKey = apiKey;
    }
    private final WebClient webClient;
    private final String apiKey;


    public Mono<OpenAIResponse> complete(final String prompt) {
        final OpenAIRequest request = new OpenAIRequest.Builder()
            .model("text-davinci-003")
            .prompt(prompt)
            .temperature(0.5)
            .maxTokens(4000)
            .topP(0.3)
            .frequencyPenalty(0.5)
            .presencePenalty(0)
            .stream(false)
            .build();

        return webClient.post()
            .uri("completions")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(OpenAIResponse.class);
    }

}
