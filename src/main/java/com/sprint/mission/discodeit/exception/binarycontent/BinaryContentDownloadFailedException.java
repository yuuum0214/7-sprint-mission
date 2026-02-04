package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentDownloadFailedException extends BinaryContentException {
    public BinaryContentDownloadFailedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
