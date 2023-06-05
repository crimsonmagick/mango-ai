package com.mangomelancholy.mangoai.adapters.outbound.chat;

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.PAL;
import static java.util.Optional.ofNullable;

import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.infrastructure.chat.ChatResponse;
import com.mangomelancholy.mangoai.infrastructure.chat.ChatResponse.ChatMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ChatUtility {

  private static final Logger log = LogManager.getLogger(ChatUtility.class);

  public static class MissingMessage extends RuntimeException {

    public MissingMessage(final String message) {
      super(message);
    }
  }

  public ExpressionValue mapResponse(final ChatResponse response) {
    if (response == null || response.choices() == null || response.choices().size() < 1
        || response.choices().get(0) == null || response.choices().get(0).message() == null) {
      log.error("Invalid response, response={}", response);
      throw new RuntimeException(String.format("Invalid response, response=%s", response));
    }
    // assumes there will only be one message in response
    final ChatMessage chatMessage = response.choices().stream()
        .findAny()
        .orElseThrow(() -> new RuntimeException("No choice provided by response."))
        .message();
    final String content = ofNullable(chatMessage)
        .orElseThrow(() -> new MissingMessage("No message included in ChatResponse."))
        .content();
    return new ExpressionValue(content, PAL);
  }
}

