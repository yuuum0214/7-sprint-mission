package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelParticipantsException extends ChannelException {
    public ChannelParticipantsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
