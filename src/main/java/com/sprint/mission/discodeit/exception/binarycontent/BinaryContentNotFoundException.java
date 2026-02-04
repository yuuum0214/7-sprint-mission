package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentNotFoundException extends BinaryContentException {
    public BinaryContentNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
