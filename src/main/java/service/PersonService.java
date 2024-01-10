package service;

import dto.request.PagingRequest;
import entity.Person;
import entity.common.QuerySearchResult;
import io.smallrye.mutiny.Uni;
import reactor.core.publisher.Mono;

public interface PersonService {
    Mono<QuerySearchResult<Person>> findAll(PagingRequest pagingRequest);

    Uni<Person> findById(Long id);

    Uni<Person> create(Person person);

    Uni<Person> update(Person person, Long id);

    Uni<Person> delete(Long id);
}
