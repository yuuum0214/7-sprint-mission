package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChannelMapper {
    private final UserMapper userMapper;

    public ChannelMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public ChannelResponseDto toDto(Channel channel){
        if(channel==null){
            return null;
        }

        Instant lastMessageAt = calculateLastMessageAt(channel);

        List<UserResponseDto> participants = channel.getReadStatuses() != null
                ? channel.getReadStatuses().stream()
                .map(readStatus -> userMapper.toDto(readStatus.getUser()))
                .toList()
                : List.of();

        return ChannelResponseDto.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName() != null ? channel.getName() : "")
                .description(channel.getDescription() != null ? channel.getDescription() : "")
                .participants(participants)
                .lastMessageAt(lastMessageAt)
                .build();
    }

    public List<ChannelResponseDto> toDtoList(List<Channel> channels){
        if(channels==null || channels.isEmpty()){
            return List.of();
        }

        return channels.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Instant calculateLastMessageAt(Channel channel){
        if(channel.getMessages() == null || channel.getMessages().isEmpty()){
            return null;
        }
        return channel.getMessages().stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }
}