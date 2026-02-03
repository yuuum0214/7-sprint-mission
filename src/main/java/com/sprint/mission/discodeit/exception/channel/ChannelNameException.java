package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNameException extends ChannelException {
    public ChannelNameException(ErrorCode errorCode) {
        super(errorCode);
    }
}
