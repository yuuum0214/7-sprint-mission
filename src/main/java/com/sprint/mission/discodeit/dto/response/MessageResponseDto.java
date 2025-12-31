package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MessageResponseDto {
    private UUID id; //messageId;
    private Instant createdAt;
    private Instant updatedAt;
    private String content;
    private UUID channelId;
    private UserResponseDto author; //userId;
    private List<UUID> attachmentIds;

    public static MessageResponseDto from (Message message){
        return MessageResponseDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .author(UserResponseDto.from(
                        message.getAuthor(),
                        message.getAuthor().getUserStatus(),
                        message.getAuthor().getProfile()
                ))
                .channelId(message.getChannel().getId())
                .attachmentIds(message.getAttachments().stream().map(BinaryContent::getId).toList())
                .createdAt(message.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
    }
}
