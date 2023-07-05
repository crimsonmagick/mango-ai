package com.mangomelancholy.mangoai.adapters.inbound.websockets

import com.mangomelancholy.mangoai.infrastructure.chat.OpenAIChatClient
import com.mangomelancholy.mangoai.infrastructure.completions.OpenAICompletionsClient
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.test.StepVerifier
import spock.lang.Specification

import java.time.Duration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AiWebSocketHandlerSpec extends Specification {

    @SpringBean
    OpenAIChatClient openAIChatClient = Mock()
    @SpringBean
    OpenAICompletionsClient openAICompletionsClient = Mock()
    @Value('${spring.webflux.base-path}')
    String basePath

    @LocalServerPort
    int serverPort

    def "test WebSocket endpoint"() {
        setup:
        WebSocketClient webSocketClient = new ReactorNettyWebSocketClient()
        URI uri = new URI("ws://localhost:${serverPort}/mango/melancholy/pal/event-emitter")

        when:
        Flux<String> response = Flux.create((FluxSink<String> sink) -> {
            webSocketClient.execute(uri, { WebSocketSession session ->
                session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .take(5)
                        .doOnNext(sink::next)
                        .doOnComplete(sink::complete)
                        .doOnError(sink::error)
                        .then()
            }).subscribe()
        })

        then:
        StepVerifier.create(response)
                .expectNext("0")
                .expectNext("1")
                .expectNext("2")
                .expectNext("3")
                .expectNext("4")
                .thenCancel()
                .verify(Duration.ofSeconds(60))
    }
}
