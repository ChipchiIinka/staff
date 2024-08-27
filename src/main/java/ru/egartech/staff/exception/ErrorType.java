package ru.egartech.staff.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    COMMON_ERROR("Повторите запрос позже", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND( "По вашему запросу ресурс не найден", HttpStatus.NOT_FOUND),
    CLIENT_ERROR( "Проверьте параметры и повторите запрос", HttpStatus.BAD_REQUEST);

    private final String text;
    private final HttpStatus httpStatus;
}
