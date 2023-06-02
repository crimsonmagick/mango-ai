package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.infrastructure.TextCompletion;
import org.springframework.stereotype.Component;

@Component
public class CompletionMapper {

  protected static final String ATTRIBUTION = ActorType.PAL + ": ";

  public String extractFragment(final TextCompletion textCompletion) {
    return textCompletion.choices().stream()
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No choice provided by completion."))
        .text();
  }

  public int lastIndexOfAttribution(final String content) {
    return content.lastIndexOf(ATTRIBUTION);
  }

  public ExpressionFragment mapFragment(final TextCompletion textCompletion, final String conversationId, final long sequenceNumber) {
    return new ExpressionFragment(extractFragment(textCompletion), conversationId, sequenceNumber);
  }

  public ExpressionValue mapResponse(final TextCompletion response) {
    final String choiceText = response.choices().get(0).text();
    final String normalizedChoiceText = normalizeChoice(choiceText);
    return new ExpressionValue(normalizedChoiceText, ActorType.PAL);
  }

  public String normalizeChoice(final String choiceText) {
    final int attributionIndex = lastIndexOfAttribution(choiceText);
    final String palResponse;
    if (attributionIndex > -1) {
      palResponse = choiceText.substring(attributionIndex + ATTRIBUTION.length());
    } else {
      palResponse = choiceText;
    }
    return palResponse;
  }
}

