package com.mangomelancholy.mangoai.application;

import com.mangomelancholy.mangoai.adapters.outbound.chat.GptChatSingletonService;
import com.mangomelancholy.mangoai.adapters.outbound.chat.GptChatStreamedService;
import com.mangomelancholy.mangoai.adapters.outbound.davinci.DavinciSingletonService;
import com.mangomelancholy.mangoai.adapters.outbound.davinci.DavinciStreamedService;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.AiSingletonService;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.AiStreamedService;
import java.security.InvalidParameterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AiServiceResolver {

  private final DavinciSingletonService davinciSingletonService;
  private final GptChatSingletonService gptChatSingletonService;
  private final DavinciStreamedService davinciStreamedService;
  private final GptChatStreamedService gptChatStreamedService;

  public AiSingletonService resolveSingletonService(final String model) {
    if (model.toLowerCase().startsWith("gpt")) {
      return gptChatSingletonService;
    }
    if (model.toLowerCase().startsWith("davinci")) {
      return davinciSingletonService;
    }
    throw new InvalidParameterException(String.format("model=\"%s\" is not supported.", model));
  }

  public AiStreamedService resolveStreamedService(final String model) {
    if (model.toLowerCase().startsWith("gpt")) {
      return gptChatStreamedService;
    }
    if (model.toLowerCase().startsWith("davinci")) {
      return davinciStreamedService;
    }
    throw new InvalidParameterException(String.format("model=\"%s\" is not supported.", model));
  }

}
