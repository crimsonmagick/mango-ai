package com.mangomelancholy.mangoai.infrastructure;

public record OpenAiCompletionsParams(String model, Boolean stream, Double temperature, Integer maxTokens, Double topP, Double frequencyPenalty,
                                Double presencePenalty) {

  public static class Builder {

    private Double frequencyPenalty;
    private Integer maxTokens;
    private String model;
    private Double presencePenalty;
    private Boolean stream;
    private Double temperature;
    private Double topP;

    public OpenAiCompletionsParams build() {
      return new OpenAiCompletionsParams(model, stream, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);
    }

    public Builder frequencyPenalty(final Double frequencyPenalty) {
      this.frequencyPenalty = frequencyPenalty;
      return this;
    }

    public Builder maxTokens(final Integer maxTokens) {
      this.maxTokens = maxTokens;
      return this;
    }

    public Builder model(final String model) {
      this.model = model;
      return this;
    }

    public Builder presencePenalty(final Double presencePenalty) {
      this.presencePenalty = presencePenalty;
      return this;
    }

    public Builder stream(final Boolean stream) {
      this.stream = stream;
      return this;
    }

    public Builder temperature(final Double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder topP(final Double topP) {
      this.topP = topP;
      return this;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

}