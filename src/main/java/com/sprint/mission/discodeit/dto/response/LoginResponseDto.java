package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponseDto {
//    private final String id;
    private final UUID id;
    private final String username; //
    private final String email; //
    private final Boolean online;
    private final BinaryContentResponseDto profile;

    public static LoginResponseDto from(User user) {
        return LoginResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .online(user.getUserStatus().isOnline())
                .profile(user.getProfile() == null ? null :
                        BinaryContentResponseDto.from(user.getProfile()))
                .build();
    }
}
