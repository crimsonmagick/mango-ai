package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ExpressionFragment;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import com.mangomelancholy.mangoai.infrastructure.TextCompletion;
import org.springframework.stereotype.Component;

@Component
public class CompletionMapper {

  protected static final String ATTRIBUTION = ActorType.PAL + ": ";
  public ExpressionValue mapResponse(final TextCompletion response) {
    final String choiceText = response.choices().get(0).text();
    final String normalizedChoiceText = normalizeChoice(choiceText);
    return new ExpressionValue(normalizedChoiceText, ActorType.PAL);
  }

  public String normalizeChoice(final String choiceText) {
    final int attributionIndex = choiceText.lastIndexOf(ATTRIBUTION);
    final String palResponse;
    if (attributionIndex > -1) {
      palResponse = choiceText.substring(attributionIndex + ATTRIBUTION.length());
    } else {
      palResponse = choiceText;
    }
    return palResponse;
  }

  public ExpressionFragment mapFragment(final TextCompletion textCompletion, final long sequenceNumber) {
    final String contentFragment = textCompletion.choices().stream()
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No choice provided by completion."))
        .text();
    return new ExpressionFragment(contentFragment, sequenceNumber);
  }
}

