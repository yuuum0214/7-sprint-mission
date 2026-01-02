package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MessageMapper {
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    public MessageMapper(UserMapper userMapper, BinaryContentMapper binaryContentMapper) {
        this.userMapper = userMapper;
        this.binaryContentMapper = binaryContentMapper;
    }

    public MessageResponseDto toDto(Message message){
        if(message==null){
            return null;
        }

        return MessageResponseDto.builder()
                .id(message.getId())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .content(message.getContent())
                .channelId(message.getChannel() != null ?  message.getChannel().getId() : null)
                .author(userMapper.toDto(message.getAuthor()))
                .attachments(binaryContentMapper.toDtoList(message.getAttachments()))
                .build();
    }

    public List<MessageResponseDto> toDtoList(List<Message> messages){
        if(messages==null || messages.isEmpty()){
            return List.of();
        }

        return messages.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
