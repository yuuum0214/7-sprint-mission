package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateRequestDto {
    private String username;
    private String email;
    private String password;

    public static UserCreateRequestDto from(User user){
        return UserCreateRequestDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
