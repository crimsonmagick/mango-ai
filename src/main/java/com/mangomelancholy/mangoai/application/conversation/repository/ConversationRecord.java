package com.mangomelancholy.mangoai.application.conversation.repository;

import com.mangomelancholy.mangoai.application.conversation.Expression;
import java.util.List;

public record ConversationRecord(String conversationId, List<Expression> expressions)  {

}
