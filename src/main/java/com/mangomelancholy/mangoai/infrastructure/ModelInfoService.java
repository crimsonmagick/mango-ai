package com.mangomelancholy.mangoai.infrastructure;

import static com.mangomelancholy.mangoai.infrastructure.ModelInfoService.ModelType.DAVINCI;
import static com.mangomelancholy.mangoai.infrastructure.ModelInfoService.ModelType.GPT_3_5;
import static com.mangomelancholy.mangoai.infrastructure.ModelInfoService.ModelType.GPT_4;

import com.knuddels.jtokkit.api.EncodingType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelInfoService {

  // TODO put in config file, map to model types
  private static final int HIGH_MAX_INPUT_TOKENS = 7000;
  private static final int HIGH_MAX_TOKENS = 8192;
  private static final int LOW_MAX_INPUT_TOKENS = 3000;
  private static final int LOW_MAX_TOKENS = 4097;
  @Value("${seeds.chat.gpt.conversation}")
  private final String chatGptSeed;
  @Value("${seeds.davinci.conversation}")
  private final String davinciSeed;


  public enum ModelType {
    GPT_3_5("gpt-3.5-turbo"), GPT_4("gpt-4"), DAVINCI("davinci");

    private final String modelString;

    ModelType(final String modelString) {
      this.modelString = modelString;
    }

    public static ModelType fromString(final String modelName) {
      if (modelName.startsWith("gpt-3")) {
        return GPT_3_5;
      } else if (modelName.startsWith("gpt-4")) {
        return GPT_4;
      } else if (modelName.startsWith("davinci")) {
        return DAVINCI;
      }
      throw new RuntimeException("Unrecognized model type.");
    }

    public String modelString() {
      return modelString;
    }
  }

  public EncodingType getEncoding(final ModelType modelType) {
    if (modelType == DAVINCI) {
      return EncodingType.R50K_BASE;
    }
    if (modelType == GPT_3_5 || modelType == GPT_4) {
      return EncodingType.CL100K_BASE;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

  public String getInitialPrompt(final ModelType model) {
    if (model == DAVINCI) {
      return davinciSeed;
    }
    if (model == GPT_3_5 || model == GPT_4) {
      return chatGptSeed;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

  public int getMaxInputTokens(final ModelType model) {
    if ((model == DAVINCI) || (model == GPT_3_5)) {
      return LOW_MAX_INPUT_TOKENS;
    } else if (model == GPT_4) {
      return HIGH_MAX_INPUT_TOKENS;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

  public int getMaxResponseTokens(final ModelType model) {
    if (model == DAVINCI || model == GPT_3_5) {
      return LOW_MAX_TOKENS - LOW_MAX_INPUT_TOKENS;
    } else if (model == GPT_4) {
      return HIGH_MAX_TOKENS - HIGH_MAX_INPUT_TOKENS;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

  public int getMessagePaddingTokens(final ModelType model) {
    if (model == DAVINCI) {
      return 0;
    } else if ((model == GPT_4) || (model == GPT_3_5)) {
      return 6;
    }
    throw new RuntimeException("Unrecognized model type.");
  }

}
