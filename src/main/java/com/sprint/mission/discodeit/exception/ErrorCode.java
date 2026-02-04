package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다", "U-001"),
    USER_EXISTS_NAME(HttpStatus.CONFLICT, "이미 존재하는 이름입니다.", "U-002"),
    USER_EXISTS_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.", "U-003"),
    //CHANNEL
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다", "C-001"),
    CHANNEL_NAME_NOT_BLANK(HttpStatus.BAD_REQUEST, "채널 이름은 필수입니다.", "C-002"),
    CHANNEL_PRIVATE_PARTICIPANTS(HttpStatus.BAD_REQUEST, "참가자는 필수입니다.", "C-003"),
    PRIVATE_CHANNEL_NOT_IMPOSABLE_UPDATED(HttpStatus.BAD_REQUEST, "private 채널은 수정할 수 없습니다.", "C-004"),
    //MESSAGE
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다.", "M-001"),
    //STATUS

    //BINARY_CONTENT
    BINARY_CONTENT_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "파일 처리 중 에러가 발생했습니다.", "B-001"),
    BINARY_CONTENT_SAVE_FAILED(HttpStatus.BAD_REQUEST, "파일 저장 실패", "B-002"),
    BINARY_LOCAL_STORAGE_INIT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "로컬 저장소 초기화 실패", "B-003"),
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.", "B-004"),
    BINARY_CONTENT_READ_FAILED(HttpStatus.BAD_REQUEST, "파일 읽기 실패", "B-005"),
    BINARY_CONTENT_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드 실패", "B-006"),
    //AUTH
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다.", "A-001");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
