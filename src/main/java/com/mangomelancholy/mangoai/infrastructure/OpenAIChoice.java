package com.mangomelancholy.mangoai.infrastructure;

public record OpenAIChoice(String text, Integer index, String logprobs, String finish_reason) {

}
