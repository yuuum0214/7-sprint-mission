package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //USER
    USER_NOT_FOUNT(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다", "U-001"),
    USER_EXISTS_NAME(HttpStatus.CONFLICT, "이미 존재하는 이름입니다.", "U-002"),
    USER_EXISTS_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.", "U-003"),
    //CHANNEL
    CHANNEL_NOT_FOUNT(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다", "C-001"),
    //MESSAGE

    //STATUS

    //BINARY_CONTENT
    BINARY_CONTENT_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "파일 처리 중 에러가 발생했습니다.", "B-001");
    //AUTH

    private final HttpStatus status;
    private final String code;
    private final String message;
}
