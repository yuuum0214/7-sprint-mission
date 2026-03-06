package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserUpdateRequestDto {
    private String newUsername;
    private String newEmail;
    private String newPassword;

    public static UserUpdateRequestDto from(User user){
        return UserUpdateRequestDto.builder()
                .newUsername(user.getUsername())
                .newEmail(user.getEmail())
                .newPassword(user.getPassword())
                .build();
    }
}
