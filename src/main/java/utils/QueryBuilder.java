package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class QueryBuilder {

    private StringBuilder query = new StringBuilder();
    private List<Object> params = new ArrayList<>();

    public QueryBuilder selectAll() {
        query.append(" SELECT * ");
        return this;
    }

    public QueryBuilder selectCount() {
        query.append(" SELECT count(*) ");
        return this;
    }

    public QueryBuilder selectField(List<String> selectFields) {
        if (ObjectUtils.isNotEmpty(selectFields)) {
            String selectClause = selectFields.stream().collect(Collectors.joining(" , "));
            query.append(String.format(" SELECT %s ", selectClause));
        } else {
            query.append(" SELECT * ");
        }
        return this;
    }


    public QueryBuilder insert(String tableName, List<String> fields, List<Object> values) {
        if (ObjectUtils.isNotEmpty(tableName) && ObjectUtils.isNotEmpty(fields) && ObjectUtils.isNotEmpty(values)) {
            String columns = fields.stream().collect(Collectors.joining(" , "));
            AtomicInteger cnt = new AtomicInteger(params.size());
            String valuePlaceholders = values.stream().map(v -> "$" + (cnt.incrementAndGet())).collect(Collectors.joining(" , "));

            query.append(String.format(" INSERT INTO %s (%s) VALUES (%s) ", tableName, columns, valuePlaceholders));

            params.addAll(values);
        } else {
            // Handle case where fields or values are empty
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder update(String tableName, Map<String, Object> values) {
        if (ObjectUtils.isNotEmpty(values)) {
            String setClause = values.entrySet().stream()
                    .map(entry -> {
                        params.add(entry.getValue());
                        return String.format(" %s = $%d ", entry.getKey(), params.size() + 1);
                    })
                    .collect(Collectors.joining(", "));

            query.append(String.format(" UPDATE %s SET %s ", tableName, setClause));

        } else {
            // Handle case where values are empty
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder select() {
        query.append(" SELECT ");
        return this;
    }

    public QueryBuilder delete() {
        query.append(" DELETE ");
        return this;
    }

    public QueryBuilder and() {
        query.append(" AND ");
        return this;
    }

    public QueryBuilder where() {
        query.append(" WHERE ");
        return this;
    }

    public QueryBuilder not() {
        query.append(" NOT ");
        return this;
    }

    public QueryBuilder having() {
        query.append(" HAVING ");
        return this;
    }

    public QueryBuilder or() {
        query.append(" OR ");
        return this;
    }

    public QueryBuilder isNull(String field) {
        String condition = " ## IS NULL ";
        condition = condition.replace("##", field);
        query.append(condition);
        return this;
    }

    public QueryBuilder isNotNull(String field) {
        String condition = " ## IS NOT NULL ";
        condition = condition.replace("##", field);
        query.append(condition);
        return this;
    }

    public QueryBuilder top(int number) {
        query.append(" TOP ").append(number);
        return this;
    }

    public QueryBuilder as(String columnName) {
        query.append(" AS ").append(columnName);
        return this;
    }

    public QueryBuilder in(String searchField, List<String> values) {
        if (ObjectUtils.isNotEmpty(values)) {
            AtomicInteger cnt = new AtomicInteger(params.size());
            String condition = String.format(" %s IN (%s) ",
                    searchField,
                    values.stream().map(v -> "$" + (cnt.incrementAndGet())).collect(Collectors.joining(", ")));
            params.addAll(values);
            query.append(condition);
        } else {
            query.append(" 1=1 ");
        }
        return this;
    }

    public QueryBuilder between(String searchField, Object value1, Object value2) {
        if (ObjectUtils.isNotEmpty(value1) && ObjectUtils.isNotEmpty(value2)) {
            String condition = String.format(" %s BETWEEN $%s AND $%s ",
                    searchField, params.size() + 1, params.size() + 2);
            params.add(value1);
            params.add(value2);
            query.append(condition);
        } else {
            query.append(" 1=1 ");
        }
        return this;
    }

    public QueryBuilder like(String searchField, String keyword) {
        if (ObjectUtils.isNotEmpty(keyword)) {
            String condition = " ## LIKE CONCAT('%',$$,'%') ";
            params.add(keyword.trim());
            String paramQuery = "$" + (params.size());
            condition = condition.replace("$$", paramQuery);
            condition = condition.replace("##", searchField);
            query.append(condition);
        } else {
            query.append(" 1=1 ");
        }
        return this;
    }


    public QueryBuilder equal(String searchField, Long status) {
        if (ObjectUtils.isNotEmpty(status) && status > 0) {
            String condition = " ## = $$ ";
            params.add(status);
            String paramQuery = "$" + (params.size());
            condition = condition.replace("$$", paramQuery);
            condition = condition.replace("##", searchField);
            query.append(condition);
        } else {
            query.append(" 1=1 ");
        }
        return this;
    }


    public QueryBuilder orderBy(String sortField, String sortBy) {
        if (ObjectUtils.isNotEmpty(sortField) && ObjectUtils.isNotEmpty(sortBy)) {
            query.append(" ORDER BY ").append(sortField).append(" ").append(sortBy);
        } else {
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder groupBy(String groupBy) {
        if (ObjectUtils.isNotEmpty(groupBy)) {
            query.append(" GROUP BY ").append(groupBy);
        } else {
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder limit(int limit) {
        if (ObjectUtils.isNotEmpty(limit)) {
            query.append(" LIMIT ").append(limit);
        } else {
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder offset(int offset) {
        if (ObjectUtils.isNotEmpty(offset)) {
            query.append(" OFFSET ").append(offset);
        } else {
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder from(String tableName) {
        query.append(" FROM ").append(tableName);
        return this;
    }

    public QueryBuilder exists(String subquery) {
        if (ObjectUtils.isNotEmpty(subquery)) {
            query.append(String.format(" EXISTS (%s) ", subquery));
        } else {
            // Handle case where subquery is empty
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder count(String columnName) {
        if (ObjectUtils.isNotEmpty(columnName)) {
            query.append(String.format(" COUNT(%s) ", columnName));
        } else {
            // Handle case where column name is empty
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder sum(String columnName) {
        if (ObjectUtils.isNotEmpty(columnName)) {
            query.append(String.format(" SUM(%s) ", columnName));
        } else {
            // Handle case where column name is empty
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder avg(String columnName) {
        if (ObjectUtils.isNotEmpty(columnName)) {
            query.append(String.format(" AVG(%s) ", columnName));
        } else {
            // Handle case where column name is empty
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder min(String columnName) {
        if (ObjectUtils.isNotEmpty(columnName)) {
            query.append(String.format(" MIN(%s) ", columnName));
        } else {
            // Handle case where column name is empty
            query.append(" ");
        }
        return this;
    }

    public QueryBuilder max(String columnName) {
        if (ObjectUtils.isNotEmpty(columnName)) {
            query.append(String.format(" MAX(%s) ", columnName));
        } else {
            // Handle case where column name is empty
            query.append(" ");
        }
        return this;
    }

    public String getQuery() {
        log.info("Query : {}", query.toString());
        return query.toString();
    }

}

