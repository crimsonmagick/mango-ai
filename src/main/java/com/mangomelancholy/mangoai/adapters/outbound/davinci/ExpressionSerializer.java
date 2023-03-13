package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType;
import org.springframework.stereotype.Component;

@Component
public class ExpressionSerializer {

  /**
   * Serializes an Expression in a format that can be used with the completions OpenAI endpoint.
   *
   * @param expression to parse
   * @return a parsed String
   */
  String serializeExpression(final ExpressionValue expression) {
    return getPrefix(expression.actor()) + expression.content();
  }

  private String getPrefix(final ActorType actorType) {
    if (actorType == ActorType.USER) {
      return "You: ";
    } else if (actorType == ActorType.SYSTEM) {
      return "System: ";
    }
    return "PAL: ";
  }

}
