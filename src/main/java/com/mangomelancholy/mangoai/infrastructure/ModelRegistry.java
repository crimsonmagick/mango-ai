package com.mangomelancholy.mangoai.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelRegistry {

  private static final int MAX_INPUT_TOKENS = 3000;
  private static final int MAX_TOKENS = 4097;
  @Value("${seeds.davinci.conversation}")
  private final String davinciSeed;

  public enum ModelType {DAVINCI}

  public String getInitialPrompt(final ModelType model) {
    if (model == ModelType.DAVINCI) {
      return davinciSeed;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

  public int getMaxInputTokens(final ModelType model) {
    if (model == ModelType.DAVINCI) {
      return MAX_INPUT_TOKENS;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

  public int getMaxResponseTokens(final ModelType model) {
    if (model == ModelType.DAVINCI) {
      return MAX_TOKENS - MAX_INPUT_TOKENS;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

}
