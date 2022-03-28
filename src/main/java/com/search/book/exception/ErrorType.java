package com.search.book.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorType {
    REQUEST_VALIDATE_ERROR(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 값입니다."),
    DUPLICATED_ERROR(HttpStatus.BAD_REQUEST.value(), "Duplicated Error"),
    NOT_FOUND_ACCOUNT(HttpStatus.NOT_FOUND.value(), "회원정보를 찾을 수 없습니다."),
    PASSWORD_ERROR(HttpStatus.NOT_EXTENDED.value(), "비밀번호가 틀립니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Service Error"),
    ALREADY_EXIST_ACCOUNT_ERROR(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 ID 입니다.");

    private final Integer statusCode;
    private final String message;
}
