package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //USER
    USER_NOT_FOUNT(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다", "U-001"),
    //CHANNEL
    CHANNEL_NOT_FOUNT(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다", "C-001");
    //MESSAGE

    //STATUS

    //BINARY_CONTENT

    //AUTH

    private final HttpStatus status;
    private final String code;
    private final String message;
}
