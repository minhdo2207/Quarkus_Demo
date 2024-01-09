package utils;

import entity.Person;
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
//    public static Mono<QuerySearchResult<?>> search(PgPool client, SqlConnection conn, int page, int pageSize, String sortField,
//                                                    String sortBy, String keyword, long tenantId) {
//        log.debug("search page={}, pageSize={},sortField={},sortBy={}, keyword={}, tenantId={}",
//                page, pageSize, sortField, sortBy, keyword, tenantId);
//        int offset = pageSize * page - pageSize;
//        List<Object> params = new ArrayList<>();
//
//        String selectQuery = String.format("SELECT * FROM %s WHERE 1 = 1 ", TableNames.PARTNER);
//        String countQuery = String.format("SELECT count(*) as count FROM %s WHERE 1 = 1 ", TableNames.PARTNER);
//
//        if (keyword != null && !keyword.isEmpty()) {
//            String condition = " AND country_code ILIKE CONCAT('%',$$,'%') ";
//            params.add(keyword.trim());
//            String paramQuery = "$" + (params.size());
//            condition = condition.replace("$$", paramQuery);
//            selectQuery = selectQuery + condition;
//            countQuery = countQuery + condition;
//        }
//
//        if (tenantId > 0) {
//            String condition = " AND tenant_id = $$ ";
//            params.add(tenantId);
//            String paramQuery = "$" + (params.size());
//            condition = condition.replace("$$", paramQuery);
//            selectQuery = selectQuery + condition;
//            countQuery = countQuery + condition;
//        }
//
//        selectQuery = selectQuery + String.format(" ORDER BY %s %s LIMIT %s OFFSET %s ", sortField, sortBy, pageSize, offset);
//
//        String finalCountQuery = countQuery;
//
//        log.debug("Select query: {}", selectQuery);
//
//        Function<SqlConnection, Uni<RowSet<Row>>> functionCount = sqlConn -> sqlConn.preparedQuery(finalCountQuery)
//                .execute(Tuple.tuple(params));
//        Uni<RowSet<Row>> count = (conn == null ? client.withConnection(functionCount) : functionCount.apply(conn));
//
//        Uni<Integer> totalUni = count.onItem()
//                .transform(pgRowSet -> pgRowSet.iterator().next().getInteger("count"));
//
//        String finalSelectQuery = selectQuery;
//        log.debug("Query Select: {}", finalSelectQuery);
//
//        Function<SqlConnection, Uni<RowSet<Row>>> functionSelect = sqlConn -> sqlConn.preparedQuery(finalSelectQuery)
//                .execute(Tuple.tuple(params));
//        Multi<Person> itemsUni = (conn == null ? client.withConnection(functionSelect)
//                : functionSelect.apply(conn))
//                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
//                .onItem().transform(row -> Person.from(row));
//
//        Mono<Integer> mono = totalUni.convert().with(UniReactorConverters.toMono());
//        Flux<Person> flux = itemsUni.convert().with(MultiReactorConverters.toFlux());
//
//        return Mono.zip(mono, flux.collectList(), (total, icc) -> {
//            QuerySearchResult<Person> qsr = new QuerySearchResult<>(total, icc);
//            qsr.setCount(total);
//            qsr.setItems(icc);
//            return qsr;
//        });
//    }
}
