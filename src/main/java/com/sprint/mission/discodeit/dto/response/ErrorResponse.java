package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private Instant timestamp;
    private String code;
    private String message;
    private Map<String, Object> details;
    private String exceptionType;
    private int status;

    public static ErrorResponse from(DiscodeitException e) {
        return ErrorResponse.builder()
                .timestamp(e.getTimestamp())
                .status(e.getErrorCode().getStatus().value())
                .code(e.getErrorCode().getCode())
                .message(e.getErrorCode().getMessage())
                .exceptionType(e.getClass().getSimpleName())
                .details(e.getDetails())
                .build();
    }

    public static ErrorResponse of(
            int status, String  code, String message, String type, Map<String, Object> details
    ) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status)
                .code(code)
                .message(message)
                .exceptionType(type)
                .details(details)
                .build();
    }
}
