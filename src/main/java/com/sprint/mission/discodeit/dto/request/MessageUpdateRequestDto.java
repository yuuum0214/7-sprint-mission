package com.sprint.mission.discodeit.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class MessageUpdateRequestDto {
    private String newContent;
}
