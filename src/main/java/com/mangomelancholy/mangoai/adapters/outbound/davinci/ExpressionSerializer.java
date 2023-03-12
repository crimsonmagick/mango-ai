package com.mangomelancholy.mangoai.adapters.outbound.davinci;

import com.mangomelancholy.mangoai.application.conversation.ExpressionValue;
import org.springframework.stereotype.Component;

@Component
public class ExpressionSerializer {

  /**
   * Serailizes an Expression in a format that can be used with the completions OpenAI endpoint.
   *
   * @param expression to parse
   * @return a parsed String
   */
  String parseExpression(final ExpressionValue expression) {
    return null;
  }

}
