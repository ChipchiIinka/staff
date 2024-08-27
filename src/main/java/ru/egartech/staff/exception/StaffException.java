package ru.egartech.staff.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StaffException extends RuntimeException {

    private final ErrorType type;

    public StaffException(ErrorType type, String massage){
        super(massage);
        this.type = type;
    }
}
