package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(ErrorResponse.from(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors
                .put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(
                ErrorResponse.of(400, "INVALID_INPUT", "입력값 검증 실패", e.getClass().getSimpleName(), errors)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e){
        log.error("Unknown error: ", e);
        return ResponseEntity.internalServerError().body(
                ErrorResponse.of(500, "SERVER_ERROR", "서버 내부 오류", e.getClass().getSimpleName(), null)
        );
    }

}
