package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFountException extends UserException{
    public UserNotFountException(ErrorCode errorCode) {
        super(errorCode);
    }
}
