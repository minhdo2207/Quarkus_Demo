package repository;

import dto.request.PagingRequest;
import entity.Person;
import entity.common.QuerySearchResult;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlConnection;
import reactor.core.publisher.Mono;

public interface PersonRepository {
    Mono<QuerySearchResult<Person>> findAll(SqlConnection conn, PagingRequest pagingRequest);

    Uni<Person> findById(SqlConnection conn, Long id);

    Uni<Person> create(SqlConnection conn, Person person);

    Uni<Person> update(SqlConnection conn, Person person, Long id);

    Uni<Person> delete(SqlConnection conn, Long id);
}
