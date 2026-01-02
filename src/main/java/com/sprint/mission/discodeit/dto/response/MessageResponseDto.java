package com.sprint.mission.discodeit.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MessageResponseDto {
    private UUID id; //messageId;
    private Instant createdAt;
    private Instant updatedAt;
    private String content;
    private UUID channelId;
    private UserResponseDto author; //userId;
    private List<BinaryContentResponseDto> attachments;

}
