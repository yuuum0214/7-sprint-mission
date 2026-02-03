package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNotUpdateException extends ChannelException{
    public ChannelNotUpdateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
