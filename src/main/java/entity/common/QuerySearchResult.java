package entity.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;


@Getter
@Setter
@ToString
public class QuerySearchResult<T> {

    @Schema(description = "Total items")
    Integer count;

    @Schema(description = "List items")
    List<T> items;

    public QuerySearchResult(){
    }

    public QuerySearchResult(Integer count, List<T> items){
        this.count = count;
        this.items = items;
    }

    public QuerySearchResult(long count, List<T> items){
        this.count = (int)count;
        this.items = items;
    }
}
