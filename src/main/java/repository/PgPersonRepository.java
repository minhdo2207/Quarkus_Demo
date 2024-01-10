package repository;

import dto.request.PagingRequest;
import entity.Person;
import entity.common.QuerySearchResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import sqlquery.PersonSQL;
import utils.*;

import java.util.List;


@ApplicationScoped
@Slf4j
public class PgPersonRepository implements PersonRepository {
    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    @Override
    public Mono<QuerySearchResult<Person>> findAll(SqlConnection conn, PagingRequest pagingRequest) {
        //Get query
        QueryBuildResult queryBuildResult = buildSelectQuery(TableNames.PERSON, pagingRequest);
        return PagingSortSearchUtil.findAll(client, conn, Person::from, queryBuildResult);
    }

    private QueryBuildResult buildSelectQuery(String tableName, PagingRequest pagingRequest) {
        //Intitialize
        int page = pagingRequest.getPage();
        int pageSize = pagingRequest.getPagesize();
        String keyword = pagingRequest.getKeyword();
        Long status = pagingRequest.getStatus();
        String sortField = pagingRequest.getSortField();
        String sortBy = pagingRequest.getSortBy();
        int offset = pageSize * page - pageSize;
        log.debug("search page={}, pageSize={},sortField={},sortBy={}, keyword={}, tenantId={},offset={}",
                page, pageSize, sortField, sortBy, keyword, status, offset);
        //Create Query
        QueryBuilder queryBuilder = new QueryBuilder();
        String selectQuery = queryBuilder.selectAll().from(tableName).where()
                .equal(PersonAttribute.ID.value(), status).and().like(PersonAttribute.NAME.value(), keyword)
                .orderBy(sortField, sortBy).limit(pageSize).offset(offset).getQuery();
        QueryBuilder countQueryBuilder = new QueryBuilder();
        String countQuery = countQueryBuilder.selectCount().from(tableName).where()
                .equal(PersonAttribute.ID.value(), status).and().like(PersonAttribute.NAME.value(), keyword).getQuery();
        //Get param
        List<Object> params = queryBuilder.getParams();
        return new QueryBuildResult(selectQuery, countQuery, params);
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
