package com.mangomelancholy.mangoai.infrastructure;

public record OpenAIUsage(Integer prompt_tokens, Integer completion_tokens, Integer total_tokens) {

}
