package com.mangomelancholy.mangoai.application.conversation.repository;

import java.util.List;

public record ConversationRecord(String conversationId, List<ExpressionRecord> expressions)  {

}
