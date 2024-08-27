package ru.egartech.staff.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.egartech.staff.model.ErrorDto;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionApiHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorDto> handleOtherException(Throwable t) {
        log.error("Got exception {}, message: {}", t.getClass(), t.getMessage());
        StaffException e = new StaffException(ErrorType.COMMON_ERROR, t.getMessage());
        return new ResponseEntity<>(
                new ErrorDto()
                        .code(e.getType().name())
                        .message(e.getType().getText())
                        .timestamp(LocalDateTime.now()), e.getType().getHttpStatus()
        );
    }

    @ExceptionHandler(StaffException.class)
    public ResponseEntity<ErrorDto> handleStaffException(StaffException exception) {
        return new ResponseEntity<>(
                new ErrorDto()
                        .code(exception.getType().name())
                        .message(exception.getType().getText())
                        .timestamp(LocalDateTime.now()), exception.getType().getHttpStatus()
        );
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorDto> handlePropertyReferenceException(PropertyReferenceException exception) {
        log.error("Got property reference exception {}, message: {}", exception.getClass(), exception.getMessage());
        StaffException e = new StaffException(ErrorType.CLIENT_ERROR, exception.getMessage());
        return new ResponseEntity<>(
                new ErrorDto()
                        .code(e.getType().name())
                        .message(e.getType().getText())
                        .timestamp(LocalDateTime.now()), e.getType().getHttpStatus()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error("Got data integrity exception {}, message: {}", exception.getClass(), exception.getMessage());
        StaffException e = new StaffException(ErrorType.CLIENT_ERROR, exception.getMessage());
        return new ResponseEntity<>(
                new ErrorDto()
                        .code(e.getType().name())
                        .message(e.getType().getText())
                        .timestamp(LocalDateTime.now()), e.getType().getHttpStatus()
        );
    }
}
