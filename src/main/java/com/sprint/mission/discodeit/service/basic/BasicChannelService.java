package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.ChannelUpdateResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotUpdateException;
import com.sprint.mission.discodeit.exception.channel.ChannelParticipantsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;
import static com.sprint.mission.discodeit.entity.ChannelType.PUBLIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicChannelService implements ChannelService {

    //의존성 주입
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Transactional
    @Override
    public ChannelResponseDto createPublicChannel(ChannelPublicCreateRequestDto channelPublicCreateRequestDto) {
        if (channelPublicCreateRequestDto.getName() == null
                || channelPublicCreateRequestDto.getName().isBlank()) {
            log.error("Channel Name is Blank : {}", channelPublicCreateRequestDto.getName());
            throw new ChannelNotFoundException(ErrorCode.CHANNEL_NAME_NOT_BLANK);
        }

        Channel channel = new Channel(channelPublicCreateRequestDto.getName(), PUBLIC, channelPublicCreateRequestDto.getDescription());
        channelRepository.save(channel);

        log.info("Created Channel : {}", channelPublicCreateRequestDto.getName());

        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    public ChannelResponseDto createPrivateChannel(ChannelPrivateCreateRequestDto channelPrivateCreateRequestDto) {
        List<UUID> participantIds = channelPrivateCreateRequestDto.getParticipantIds();
        if (participantIds == null || participantIds.size() <= 1) {
            log.error("Less Participant : {} ", participantIds);
            throw new ChannelParticipantsException(ErrorCode.CHANNEL_PRIVATE_PARTICIPANTS);
        }
        Channel channel = new Channel(PRIVATE);
        channel = channelRepository.save(channel);

        //유저별 ReadStatus 생성
        for (UUID userId : participantIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
            ReadStatus readStatus = new ReadStatus(user, channel);
            channel.getReadStatuses().add(readStatus);
            readStatusRepository.save(readStatus);
        }

        List<String> usernames = participantIds.stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND))
                        .getUsername())
                .toList();

        log.info("Created Private Channel : {}", usernames);

        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAllAvailableForUser(userId);

        return channels.stream()
                .map(channelMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public List<ChannelResponseDto> findAll() {
        List<Channel> channels = channelRepository.findAllWithParticipants();

        return channels.stream()
                .map(channelMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelUpdateResponseDto findById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND));
        //가장 최근 메시지 시간 정보 포함
        Instant lastMessageAt = messageRepository.findLastByChannel(channel).orElse(null);

        //PRIVATE의 경우 참여한 user id 포함
        List<UUID> participantIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findById(channelId).stream()
                    .map(rs -> rs.getUser().getId())
                    .toList();
        }

        return ChannelUpdateResponseDto.from(channel);
    }

    @Transactional
    @Override
    public ChannelResponseDto updateChannel(UUID uuid, ChannelUpdateRequestDto channelUpdateRequestDto) {
        Channel channel = channelRepository.findById(uuid)
                .orElseThrow(() -> new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND));
        if (channel.getType() == PRIVATE) {
            throw new ChannelNotUpdateException(ErrorCode.PRIVATE_CHANNEL_NOT_IMPOSABLE_UPDATED);
        }

        channel.setUpdate(channelUpdateRequestDto.getNewName(), channelUpdateRequestDto.getNewDescription());

        log.info("Update Channel Name : {}", channelUpdateRequestDto.getNewName());
        log.info("Update Channel Description : {}", channelUpdateRequestDto.getNewDescription());

        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channelRepository.findById(channelId).orElse(null);
        if (channel == null) return;

        log.info("Delete Channel Id: {}", channel.getId());
        log.info("Delete Channel Name: {}", channel.getName());
        channelRepository.delete(channel);
    }
}
