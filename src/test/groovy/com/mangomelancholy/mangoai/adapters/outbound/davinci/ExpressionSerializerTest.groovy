package com.mangomelancholy.mangoai.adapters.outbound.davinci

import com.mangomelancholy.mangoai.application.conversation.ExpressionValue
import spock.lang.Specification

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.*

class ExpressionSerializerTest extends Specification {

    def underTest

    def setup() {
        underTest = new ExpressionSerializer()
    }

    def "serializeExpression - serialized expressions should be properly prefixed"() {
        when:
        def serializedText = underTest.serializeExpression(new ExpressionValue(content, actor))

        then:
        serializedText == expected

        where:
        content                                                            | actor          | expected
        "Hey there, how can I help you?"                                   | PAL            | "PAL: Hey there, how can I help you?"
        "You are a chatbot"                                                | SYSTEM         | "System: You are a chatbot"
        "Hello, could you teach me the alphabet?"                          | USER           | "You: Hello, could you teach me the alphabet?"
        "HAL is a chatbot assistant that strives to eject you into space." | INITIAL_PROMPT | "HAL is a chatbot assistant that strives to eject you into space."

    }
}