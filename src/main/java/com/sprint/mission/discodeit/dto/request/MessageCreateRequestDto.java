package com.sprint.mission.discodeit.dto.request;

import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageCreateRequestDto {
    private UUID authorId;
    private UUID channelId;
    private String content;
}
