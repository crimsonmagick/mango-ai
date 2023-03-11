package com.mangomelancholy.mangoai.application.ports.secondary;

import java.util.List;

public record ConversationRecord(String conversationId, List<ExpressionRecord> expressions)  {

}
