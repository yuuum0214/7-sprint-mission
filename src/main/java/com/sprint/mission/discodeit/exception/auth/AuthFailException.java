package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class AuthFailException extends AuthException {
    public AuthFailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
