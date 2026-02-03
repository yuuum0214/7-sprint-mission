package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentBadRequestException extends BinaryContentException {
    public BinaryContentBadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
