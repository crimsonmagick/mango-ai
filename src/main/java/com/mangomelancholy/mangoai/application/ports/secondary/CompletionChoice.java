package com.mangomelancholy.mangoai.application.ports.secondary;

import java.util.Map;

public record CompletionChoice(String text, int index, Map<String, Double> logprobs, String finish_reason) {

}
