package com.mangomelancholy.mangoai.application.ports.secondary;

import java.util.List;

public record TextCompletion(String id, String object, long created, List<CompletionChoice> choices, String model) {

}
