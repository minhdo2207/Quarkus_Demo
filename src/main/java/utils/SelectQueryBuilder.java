package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectQueryBuilder {

    private StringBuilder selectQuery = new StringBuilder();
    private List<Object> params = new ArrayList<>();

    public SelectQueryBuilder selectAll() {
        selectQuery.append(" SELECT * ");
        return this;
    }

    public SelectQueryBuilder selectCount() {
        selectQuery.append(" SELECT count(*) ");
        return this;
    }

    public SelectQueryBuilder select(List<String> selectFields) {
        if (ObjectUtils.isNotEmpty(selectFields)) {
            String selectClause = selectFields.stream().collect(Collectors.joining(", "));
            selectQuery.append(String.format(" SELECT %s ", selectClause));
        } else {
            selectQuery.append(" SELECT * ");
        }
        return this;
    }

    public SelectQueryBuilder and() {
        selectQuery.append(" AND ");
        return this;
    }

    public SelectQueryBuilder where() {
        selectQuery.append(" WHERE ");
        return this;
    }

    public SelectQueryBuilder or() {
        selectQuery.append(" OR ");
        return this;
    }

    public SelectQueryBuilder searchLike(String searchField, String keyword) {
        if (ObjectUtils.isNotEmpty(keyword)) {
            String condition = " ## LIKE CONCAT('%',$$,'%') ";
            params.add(keyword.trim());
            String paramQuery = "$" + (params.size());
            condition = condition.replace("$$", paramQuery);
            condition = condition.replace("##", searchField);
            selectQuery.append(condition);
        } else {
            selectQuery.append(" 1=1 ");
        }
        return this;
    }


    public SelectQueryBuilder searchEqual(String searchField, Long status) {
        if (ObjectUtils.isNotEmpty(status) && status > 0) {
            String condition = " ## = $$ ";
            params.add(status);
            String paramQuery = "$" + (params.size());
            condition = condition.replace("$$", paramQuery);
            condition = condition.replace("##", searchField);
            selectQuery.append(condition);
        } else {
            selectQuery.append(" 1=1 ");
        }
        return this;
    }


    public SelectQueryBuilder orderBy(String sortField, String sortBy) {
        if (ObjectUtils.isNotEmpty(sortField) && ObjectUtils.isNotEmpty(sortBy)) {
            selectQuery.append(" ORDER BY ").append(sortField).append(" ").append(sortBy);
        } else {
            selectQuery.append(" ");
        }
        return this;
    }

    public SelectQueryBuilder groupBy(String groupBy) {
        if (ObjectUtils.isNotEmpty(groupBy)) {
            selectQuery.append(" GROUP BY ").append(groupBy);
        } else {
            selectQuery.append(" ");
        }
        return this;
    }

    public SelectQueryBuilder limit(int limit) {
        if (ObjectUtils.isNotEmpty(limit)) {
            selectQuery.append(" LIMIT ").append(limit);
        } else {
            selectQuery.append(" ");
        }
        return this;
    }

    public SelectQueryBuilder offset(int offset) {
        if (ObjectUtils.isNotEmpty(offset)) {
            selectQuery.append(" OFFSET ").append(offset);
        } else {
            selectQuery.append(" ");
        }
        return this;
    }

    public SelectQueryBuilder from(String tableName) {
        selectQuery.append(" FROM ").append(tableName);
        return this;
    }

    public String getSelectQuery() {
        return selectQuery.toString();
    }

}

