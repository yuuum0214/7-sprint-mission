package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class MessageCreateRequestDto {
    private UUID authorId;
    private UUID channelId;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
