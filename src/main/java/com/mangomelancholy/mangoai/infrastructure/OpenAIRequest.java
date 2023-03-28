package com.mangomelancholy.mangoai.infrastructure;

import java.util.List;

public record OpenAIRequest(String model, String prompt, double temperature, int max_tokens, double top_p, double frequency_penalty, double presence_penalty, boolean stream, List<String> stop) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String model;
    private String prompt;
    private double temperature;
    private int maxTokens;
    private double topP;
    private double frequencyPenalty;
    private double presencePenalty;
    private boolean stream;
    private List<String> stop;

    public Builder() {}

    public Builder model(String model) {
      this.model = model;
      return this;
    }

    public Builder prompt(String prompt) {
      this.prompt = prompt;
      return this;
    }

    public Builder temperature(double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder maxTokens(int maxTokens) {
      this.maxTokens = maxTokens;
      return this;
    }

    public Builder topP(double topP) {
      this.topP = topP;
      return this;
    }

    public Builder frequencyPenalty(double frequencyPenalty) {
      this.frequencyPenalty = frequencyPenalty;
      return this;
    }

    public Builder presencePenalty(double presencePenalty) {
      this.presencePenalty = presencePenalty;
      return this;
    }

    public Builder stream(boolean stream) {
      this.stream = stream;
      return this;
    }

    public Builder stop(final List<String> stop) {
      this.stop = stop;
      return this;
    }

    public OpenAIRequest build() {
      return new OpenAIRequest(model, prompt, temperature, maxTokens, topP, frequencyPenalty, presencePenalty, stream, stop);
    }
  }

}
