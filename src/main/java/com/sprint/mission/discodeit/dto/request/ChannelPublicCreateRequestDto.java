package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Public Channel 생성 정보")
public class ChannelPublicCreateRequestDto {

    @NotBlank(message = "채널명은 필수입니다.")
    private String name;

    @NotBlank(message = "채널 설명은 필수입니다.")
    private String description;
}
