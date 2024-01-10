package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
// Class for the query building
public class QueryBuildResult {
    private String selectQuery;
    private String countQuery;
    private List<Object> params;


}