package com.mangomelancholy.mangoai.application.conversation.ports.primary;

import java.time.ZonedDateTime;

public record ConversationSummary(String conversationId, String summary, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
}
