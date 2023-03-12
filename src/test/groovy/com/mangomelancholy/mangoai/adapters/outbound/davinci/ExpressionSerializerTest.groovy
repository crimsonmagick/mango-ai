package com.mangomelancholy.mangoai.adapters.outbound.davinci

import com.mangomelancholy.mangoai.application.conversation.ExpressionValue
import spock.lang.Specification

import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.PAL
import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.SYSTEM
import static com.mangomelancholy.mangoai.application.conversation.ExpressionValue.ActorType.USER

class ExpressionSerializerTest extends Specification {

    def underTest

    def setup() {
        underTest = new ExpressionSerializer()
    }

    def "ParseExpression - parsed expressions should be properly prefixed"() {
        when:
        def parsedText = underTest.parseExpression(new ExpressionValue(content, actor))

        then:
        parsedText == expected

        where:
        content                                   | actor  | expected
        "Hey there, how can I help you?"          | PAL    | "PAL: Hey there, how can I help you?"
        "You are a chatbot"                       | SYSTEM | "You are a chatbot"
        "Hello, could you teach me the alphabet?" | USER   | "You: Hello, could you teach me the alphabet?"

    }
}