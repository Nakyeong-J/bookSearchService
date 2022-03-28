package com.search.book.model.response.common;

import com.search.book.exception.ErrorType;
import lombok.Data;

@Data
public class ErrorInfo {
    private Integer code;
    private String message;

    public ErrorInfo(ErrorType errorType) {
        this.code = errorType.getStatusCode();
        this.message = errorType.getMessage();
    }
}
