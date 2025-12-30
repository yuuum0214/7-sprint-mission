package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ChannelResponseDto {

    private UUID id;
    private ChannelType type;
    private String name;
    private String description;
    private List<UserResponseDto> participantIds;
    private Instant lastMessageAt;

    public static ChannelResponseDto from(Channel channel , Instant lastMessageAt, List<UserResponseDto> participantIds) {
        return ChannelResponseDto.builder()
                .id(channel.getId())
                .name(channel.getName())
                .type(channel.getType())
                .description(channel.getDescription())
                .lastMessageAt(lastMessageAt)
                .participantIds(participantIds)
                .build();
    }
}
