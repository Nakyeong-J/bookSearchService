package com.search.book.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaInfo {

    @JsonProperty(value = "total_count")
    private Integer totalCount;

    @JsonProperty(value = "pageable_count")
    private Integer pageableCount;

    @JsonProperty(value = "is_end")
    private Boolean isEnd;
}
