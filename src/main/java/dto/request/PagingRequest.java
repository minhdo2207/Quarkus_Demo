package dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagingRequest {
    private int page;
    private int pagesize;
    private String keyword;
    private Long status;
    private String sortField;
    private String sortBy;

}
