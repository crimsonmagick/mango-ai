package com.mangomelancholy.mangoai.infrastructure;

public record CompletionChoice(String text, Integer index, String logprobs, String finish_reason) {

}
