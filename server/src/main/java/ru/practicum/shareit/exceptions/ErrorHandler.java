package ru.practicum.shareit.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({AlreadyExistException.class}) //409
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflictHandle(Exception e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({NotExistException.class})    //404
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundHandle(Exception e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ValidationException.class})  //400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequestHandle(Exception e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @Getter
    @RequiredArgsConstructor
    private static class ErrorResponse {
        private final String error;
        private final HttpStatus status;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private final LocalDateTime time = LocalDateTime.now();
    }

}