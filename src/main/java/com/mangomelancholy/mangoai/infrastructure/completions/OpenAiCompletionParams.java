package com.mangomelancholy.mangoai.infrastructure.completions;

import lombok.Builder;

@Builder
public record OpenAiCompletionParams(String model, Boolean stream, Double temperature, Integer maxTokens, String prompt,
                                     Double topP, Double frequencyPenalty, Double presencePenalty) {

}