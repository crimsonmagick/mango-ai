package com.mangomelancholy.mangoai.adapters.outbound;

import static reactor.core.publisher.Flux.fromIterable;

import com.mangomelancholy.mangoai.application.conversation.ports.secondary.ConversationRecord;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.ConversationRepository;
import com.mangomelancholy.mangoai.application.conversation.ports.secondary.ExpressionRecord;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Primary
@Repository
@RequiredArgsConstructor
public class ConversationRepositoryH2Impl implements ConversationRepository {

  private static final Logger log = LogManager.getLogger(ConversationRepositoryH2Impl.class);
  private final ConnectionFactory connectionFactory;

  @Override
  public Mono<ConversationRecord> create(final ConversationRecord newConversation) {
    final String conversationId = UUID.randomUUID().toString();
    return Mono.usingWhen(connectionFactory.create(),
            connection -> Mono.from(connection.beginTransaction())
                .then(
                    Mono.from(connection.createStatement("INSERT INTO CONVERSATIONS (ID) VALUES (?)")
                            .bind(0, conversationId)
                            .execute())
                        .thenMany(fromIterable(newConversation.expressions()))
                        .index()
                        .flatMap(expressionTuple ->
                            connection.createStatement(
                                    "INSERT INTO EXPRESSIONS (CONTENT, ACTOR_TYPE, CONVERSATION_ID, SEQUENCE_NUMBER) VALUES (?, ?, ?, ?)")
                                .bind(0, expressionTuple.getT2().content())
                                .bind(1, expressionTuple.getT2().actor())
                                .bind(2, conversationId)
                                .bind(3, expressionTuple.getT1())
                                .execute()
                        )
                        .then(Mono.from(connection.commitTransaction()))
                        .doOnError(throwable -> log.error(
                            "Failed to create conversation with conversationId={}", conversationId,
                            throwable))
                        .onErrorResume(throwable -> Mono.from(connection.rollbackTransaction())
                            .then(Mono.error(throwable)))),
            Connection::close)
        .then(Mono.just(new ConversationRecord(conversationId, newConversation.expressions())));
  }

  @Override
  public Mono<ConversationRecord> getConversation(final String conversationId) {
    return getExpressions(conversationId)
        .collectList()
        .map(expressionRecords -> new ConversationRecord(conversationId, expressionRecords));
  }

  @Override
  public Flux<String> getConversationIds() {
    return Flux.empty();
  }

  @Override
  public Flux<ExpressionRecord> getExpressions(final String conversationId) {
    return Flux.usingWhen(connectionFactory.create(),
            connection -> connection
                .createStatement(
                    "SELECT CONTENT, ACTOR_TYPE, CONVERSATION_ID, SEQUENCE_NUMBER FROM EXPRESSIONS WHERE CONVERSATION_ID = ? ORDER BY SEQUENCE_NUMBER")
                .bind(0, conversationId)
                .execute(),
            Connection::close)
        .flatMap(result -> result.map((row, meta) -> {
          final String content = row.get("CONTENT", String.class);
          final String actorType = row.get("ACTOR_TYPE", String.class);
          return new ExpressionRecord(content, actorType);
        }));
  }


  @Override
  public Mono<ConversationRecord> update(final ConversationRecord conversation) {
    return Mono.empty();
  }
}
