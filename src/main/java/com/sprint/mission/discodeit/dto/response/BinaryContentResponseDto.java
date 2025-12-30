package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Getter
@Builder
public class BinaryContentResponseDto {

    private UUID id;
    private String fileName;
    private long size;
    private String contentType;

//    @Schema(type = "string", format = "byte")
//    private String bytes;
//    private Instant createdAt;

    public static BinaryContentResponseDto from(BinaryContent binaryContent) {
        return BinaryContentResponseDto.builder()
                .id(binaryContent.getId())
                .contentType(binaryContent.getContentType())
                .fileName(binaryContent.getFileName())
                .size(binaryContent.getSize())
////                .bytes(Base64.getEncoder().encodeToString(binaryContent.getSize()))
//                .createdAt(binaryContent.getCreatedAt())
                .build();
    }
}
