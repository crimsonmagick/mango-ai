package com.mangomelancholy.mangoai.infrastructure;

import java.util.List;

public record OpenAIResponse(String id, String object, Long created, String model, List<OpenAIChoice> choices, OpenAIUsage usage) {

}
