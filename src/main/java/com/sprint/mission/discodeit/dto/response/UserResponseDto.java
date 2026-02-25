package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private BinaryContentResponseDto profile;
    private Boolean online;
}
