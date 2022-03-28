package com.search.book.model.response.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnData {
    private boolean hasError;
    private ErrorInfo errorInfo;
    private Object resultData;
}
