package com.mangomelancholy.mangoai.infrastructure;

import static com.mangomelancholy.mangoai.infrastructure.ModelRegistry.ModelType.CHAT_GPT;
import static com.mangomelancholy.mangoai.infrastructure.ModelRegistry.ModelType.DAVINCI;

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
  @Value("${seeds.chat.gpt.conversation}")
  private final String chatGptSeed;


  public enum ModelType {CHAT_GPT, DAVINCI}

  public String getInitialPrompt(final ModelType model) {
    if (model == DAVINCI) {
      return davinciSeed;
    }
    if (model == CHAT_GPT) {
      return chatGptSeed;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

  public int getMaxInputTokens(final ModelType model) {
    if ((model == DAVINCI) || (model == CHAT_GPT)) {
      return MAX_INPUT_TOKENS;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

  public int getMaxResponseTokens(final ModelType model) {
    if (model == DAVINCI || model == CHAT_GPT) {
      return MAX_TOKENS - MAX_INPUT_TOKENS;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

}
