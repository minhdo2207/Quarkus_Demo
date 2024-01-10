package utils;

import dto.request.PagingRequest;
import entity.common.QuerySearchResult;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.converters.multi.MultiReactorConverters;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


@Slf4j
public class PagingSortSearchUtil {
    public static <T> Mono<QuerySearchResult<T>> findAll(PgPool client, SqlConnection conn, Function<Row, T> rowMapper, QueryBuildResult queryBuildResult) {
        //Get query
        String selectQuery = queryBuildResult.getSelectQuery();
        String countQuery = queryBuildResult.getCountQuery();
        //Get param
        List<Object> params = queryBuildResult.getParams();

        //Count final number of records
        Uni<Integer> totalUni = countNumberOfRecords(client, conn, countQuery, params);
        //All Records
        Multi<T> itemsUni = selectAllRecords(client, conn, selectQuery, params, rowMapper);

        //Convert
        Mono<Integer> mono = totalUni.convert().with(UniReactorConverters.toMono());
        Flux<T> flux = itemsUni.convert().with(MultiReactorConverters.toFlux());

        return Mono.zip(mono, flux.collectList(), (total, icc) -> {
            QuerySearchResult<T> qsr = new QuerySearchResult<>(total, icc);
            qsr.setCount(total);
            qsr.setItems(icc);
            return qsr;
        });
    }

    private static <T> Multi<T> selectAllRecords(PgPool client, SqlConnection conn, String selectQuery, List<Object> params, Function<Row, T> rowMapper) {
        log.debug("Query Select: {}", selectQuery);

        Function<SqlConnection, Uni<RowSet<Row>>> functionSelect = sqlConn -> sqlConn.preparedQuery(selectQuery)
                .execute(Tuple.tuple(params));
        return (conn == null ? client.withConnection(functionSelect)
                : functionSelect.apply(conn))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(rowMapper);
    }

    private static Uni<Integer> countNumberOfRecords(PgPool client, SqlConnection conn, String finalCountQuery, List<Object> params) {
        log.debug("Count query: {}", finalCountQuery);
        Function<SqlConnection, Uni<RowSet<Row>>> functionCount = sqlConn -> sqlConn.preparedQuery(finalCountQuery)
                .execute(Tuple.tuple(params));
        Uni<RowSet<Row>> count = (conn == null ? client.withConnection(functionCount) : functionCount.apply(conn));
        return count.onItem()
                .transform(pgRowSet -> pgRowSet.iterator().next().getInteger("count"));
    }

}
