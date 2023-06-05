package com.mangomelancholy.mangoai.infrastructure;

import static com.mangomelancholy.mangoai.infrastructure.ModelInfoService.ModelType.CHAT_GPT;
import static com.mangomelancholy.mangoai.infrastructure.ModelInfoService.ModelType.DAVINCI;

import com.knuddels.jtokkit.api.EncodingType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelInfoService {

  private static final int MAX_INPUT_TOKENS = 3000;
  private static final int MAX_TOKENS = 4097;
  @Value("${seeds.chat.gpt.conversation}")
  private final String chatGptSeed;
  @Value("${seeds.davinci.conversation}")
  private final String davinciSeed;


  public enum ModelType {
    CHAT_GPT, DAVINCI;

    public static ModelType fromString(final String modelName) {
      if (modelName.startsWith("gpt")) {
        return CHAT_GPT;
      } else {
        return DAVINCI;
      }
    }
  }

  public EncodingType getEncoding(final ModelType model) {
    if (model == DAVINCI) {
      return EncodingType.R50K_BASE;
    }
    if (model == CHAT_GPT) {
      return EncodingType.CL100K_BASE;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

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
