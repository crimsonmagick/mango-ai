package com.mangomelancholy.mangoai.application;

import com.mangomelancholy.mangoai.adapters.outbound.chat.GptChatSingletonService;
import com.mangomelancholy.mangoai.adapters.outbound.davinci.DavinciSingletonService;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.AISingletonService;
import java.security.InvalidParameterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AiServiceResolver {

  private final DavinciSingletonService davinciSingletonService;
  private final GptChatSingletonService gptChatSingletonService;

  public AISingletonService resolveSingletonService(final String model) {
    if (model.toLowerCase().startsWith("gpt")) {
      return gptChatSingletonService;
    }
    if (model.toLowerCase().startsWith("davinci")) {
      return davinciSingletonService;
    }
    throw new InvalidParameterException(String.format("model=\"%s\" is not supported.", model));
  }

}
