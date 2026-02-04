package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentInitFailedException extends  BinaryContentException {
    public BinaryContentInitFailedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
