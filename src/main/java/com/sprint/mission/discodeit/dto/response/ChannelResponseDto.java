package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;
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

}
