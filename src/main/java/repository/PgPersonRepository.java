package repository;

import entity.Person;
import entity.common.QuerySearchResult;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.converters.multi.MultiReactorConverters;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sqlquery.PersonSQL;
import utils.TableNames;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
@Slf4j
public class PgPersonRepository implements PersonRepository {
    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    @Override
    public Mono<QuerySearchResult<Person>> findAll(SqlConnection conn, int page, int pageSize, String keyword, Long status, String sortField, String sortBy) {
//        return (conn == null ? client.withConnection(this::findAllWithConnection) : findAllWithConnection(conn))
//                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
//                .onItem().transform(Person::from);

        log.debug("search page={}, pageSize={},sortField={},sortBy={}, keyword={}, tenantId={}",
                page, pageSize, sortField, sortBy, keyword, status);
        int offset = pageSize * page - pageSize;
        List<Object> params = new ArrayList<>();

        String selectQuery = String.format("SELECT * FROM %s WHERE 1 = 1 ", TableNames.PERSON);
        String countQuery = String.format("SELECT count(*) as count FROM %s WHERE 1 = 1 ", TableNames.PERSON);

        if (ObjectUtils.isNotEmpty(keyword)) {
            String condition = " AND name LIKE CONCAT('%',$$,'%') ";
            params.add(keyword.trim());
            String paramQuery = "$" + (params.size());
            condition = condition.replace("$$", paramQuery);
            selectQuery = selectQuery + condition;
            countQuery = countQuery + condition;
        }

        if (ObjectUtils.isNotEmpty(status)) {
            if (status > 0) {
                String condition = " AND id = $$ ";
                params.add(status);
                String paramQuery = "$" + (params.size());
                condition = condition.replace("$$", paramQuery);
                selectQuery = selectQuery + condition;
                countQuery = countQuery + condition;
            }
        }

        selectQuery = selectQuery + String.format(" ORDER BY %s %s LIMIT %s OFFSET %s ", sortField, sortBy, pageSize, offset);

        String finalCountQuery = countQuery;

        log.debug("Select query: {}", selectQuery);

        Function<SqlConnection, Uni<RowSet<Row>>> functionCount = sqlConn -> sqlConn.preparedQuery(finalCountQuery)
                .execute(Tuple.tuple(params));
        Uni<RowSet<Row>> count = (conn == null ? client.withConnection(functionCount) : functionCount.apply(conn));

        Uni<Integer> totalUni = count.onItem()
                .transform(pgRowSet -> pgRowSet.iterator().next().getInteger("count"));

        String finalSelectQuery = selectQuery;
        log.debug("Query Select: {}", finalSelectQuery);

        Function<SqlConnection, Uni<RowSet<Row>>> functionSelect = sqlConn -> sqlConn.preparedQuery(finalSelectQuery)
                .execute(Tuple.tuple(params));
        Multi<Person> itemsUni = (conn == null ? client.withConnection(functionSelect)
                : functionSelect.apply(conn))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(row -> Person.from(row));

        Mono<Integer> mono = totalUni.convert().with(UniReactorConverters.toMono());
        Flux<Person> flux = itemsUni.convert().with(MultiReactorConverters.toFlux());

        return Mono.zip(mono, flux.collectList(), (total, icc) -> {
            QuerySearchResult<Person> qsr = new QuerySearchResult<>(total, icc);
            qsr.setCount(total);
            qsr.setItems(icc);
            return qsr;
        });
    }

    @Override
    public Uni<Person> findById(SqlConnection conn, Long id) {
        return (conn == null ? client.withConnection(c -> findByIdWithConnection(c, id)) : findByIdWithConnection(conn, id))
                .onItem().transform(m -> m.iterator().hasNext() ? Person.from(m.iterator().next()) : null);
    }

    @Override
    public Uni<Person> create(SqlConnection conn, Person person) {
        return (conn == null ? client.withConnection(c -> createWithConnection(c, person)) : createWithConnection(conn, person))
                .onItem().transform(m -> m.iterator().hasNext() ? Person.from(m.iterator().next()) : null);
    }

    @Override
    public Uni<Person> update(SqlConnection conn, Person person, Long id) {
        return (conn == null ? client.withConnection(c -> updateWithConnection(c, person, id)) : updateWithConnection(conn, person, id))
                .onItem().transform(m -> m.iterator().hasNext() ? Person.from(m.iterator().next()) : null);
    }

    @Override
    public Uni<Person> delete(SqlConnection conn, Long id) {
        return (conn == null ? client.withConnection(c -> deleteWithConnection(c, id)) : deleteWithConnection(conn, id))
                .onItem().transform(m -> m.iterator().hasNext() ? Person.from(m.iterator().next()) : null);
    }

    private Uni<RowSet<Row>> findAllWithConnection(SqlConnection conn) {
        return conn.query(PersonSQL.FIND_ALL_PERSON).execute();
    }


    private Uni<RowSet<Row>> findByIdWithConnection(SqlConnection conn, Long id) {
        return conn.preparedQuery(PersonSQL.FIND_PERSON_BY_ID).execute(Tuple.of(id));
    }


    private Uni<RowSet<Row>> createWithConnection(SqlConnection conn, Person person) {
        return conn.preparedQuery(PersonSQL.CREATE_PERSON)
                .execute(Tuple.of(person.getName(), person.getAge(), person.getEmail()));
    }


    private Uni<RowSet<Row>> updateWithConnection(SqlConnection conn, Person person, Long id) {
        return conn.preparedQuery(PersonSQL.UPDATE_PERSON)
                .execute(Tuple.of(person.getName(), person.getAge(), person.getEmail(), id));
    }

    private Uni<RowSet<Row>> deleteWithConnection(SqlConnection conn, Long id) {
        return conn.preparedQuery(PersonSQL.DELETE_PERSON).execute(Tuple.of(id));
    }
}
