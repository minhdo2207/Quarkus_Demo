package service;

import entity.Person;
import entity.common.QuerySearchResult;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import repository.PersonRepository;

@ApplicationScoped
@Slf4j
public class PersonServiceImpl implements PersonService {

    @Inject
    PersonRepository personRepository;

    @Override
    public Mono<QuerySearchResult<Person>> findAll(int page, int pageSize, String keyword, Long status, String sortField, String sortBy) {
        return personRepository.findAll(null, page, pageSize, keyword, status, sortField, sortBy);
    }

    @Override
    public Uni<Person> findById(Long id) {
        return personRepository.findById(null, id);
    }

    @Override
    public Uni<Person> create(Person person) {
        return personRepository.create(null, person);
    }

    @Override
    public Uni<Person> update(Person person, Long id) {
        return personRepository.update(null, person, id);
    }

    @Override
    public Uni<Person> delete(Long id) {
        return personRepository.delete(null, id);
    }
}
