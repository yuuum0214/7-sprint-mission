package com.sprint.mission.discodeit.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import lombok.*;

import java.time.Instant;
import java.util.*;

@Getter
@Builder
public class ChannelResponseDto {

    private UUID id;
    private ChannelType type;
    private String name;
    private String description;
    private List<UserResponseDto> participants;
    private Instant lastMessageAt;

    public static ChannelResponseDto from(Channel channel) {
        Instant lastMessageAt = channel.getMessages() != null
                && !channel.getMessages().isEmpty()
                ? channel.getMessages().stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null)
                : null;
        List<UserResponseDto> participants = channel.getReadStatuses() != null
                ? channel.getReadStatuses().stream()
                .map(rs -> UserResponseDto.from(
                        rs.getUser(),
                        rs.getUser().getUserStatus(),
                        rs.getUser().getProfile()
                )).toList()
                : new ArrayList<>();

        return ChannelResponseDto.builder()
                .id(channel.getId())
                .name(channel.getName() != null ? channel.getName() : "")
                .type(channel.getType())
                .description(channel.getDescription() != null ? channel.getDescription() : "")
                .lastMessageAt(lastMessageAt)
                .participants(participants)
                .build();

    }
}
