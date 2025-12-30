package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.ChannelUpdateResponseDto;
import com.sprint.mission.discodeit.entity.*;
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
import java.util.stream.Collectors;

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

    @Transactional
    @Override
    public ChannelUpdateResponseDto createPublicChannel(ChannelPublicCreateRequestDto channelPublicCreateRequestDto) {
        if (channelPublicCreateRequestDto.getName() == null
                || channelPublicCreateRequestDto.getName().isBlank()) {
            throw new IllegalStateException("채널 이름이 필요합니다.");
        }

        Channel channel = new Channel(channelPublicCreateRequestDto.getName(), PUBLIC, channelPublicCreateRequestDto.getDescription());
        channelRepository.save(channel);

        return ChannelUpdateResponseDto.from(channel);
    }

    @Transactional
    @Override
    public ChannelUpdateResponseDto createPrivateChannel(ChannelPrivateCreateRequestDto channelPrivateCreateRequestDto) {
        List<UUID> participantIds = channelPrivateCreateRequestDto.getParticipantIds();
        if (participantIds == null || participantIds.size() <= 1) {
            throw new IllegalStateException("2명 이상의 참여 유저가 있어야 합니다.");
        }
        Channel channel = new Channel(PRIVATE);
        channelRepository.save(channel);

        //유저별 ReadStatus 생성
        for (UUID userId : participantIds) {
            User user =  userRepository.findById(userId)
                    .orElseThrow(()->new IllegalArgumentException("존재하지 않는 유저입니다"));
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
        }
        return ChannelUpdateResponseDto.from(channel);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelUpdateResponseDto findById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));
        //가장 최근 메시지 시간 정보 포함
        Instant lastMessageAt = messageRepository.findLastByChannel(channel).orElse(null);

        //PRIVATE의 경우 참여한 user id 포함
        List<UUID> participantIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findById(channelId).stream()
                    .map(rs->rs.getUser().getId())
                    .collect(Collectors.toList());
        }

        return ChannelUpdateResponseDto.from(channel);
    }

//    @Transactional(readOnly = true)
//    @Override
//    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
//        return channelRepository.findAll().stream()
//                .filter(c -> c.getType() == PUBLIC
//                        || (c.getType() == PRIVATE && c.getParticipantIds().contains(userId)))
//                .map(c -> {
//                    Instant lastMessageAt = messageRepository.findLastByChannel(c.getId()).orElse(null);
//                    List<UUID> participantIds = (c.getType() == PRIVATE) ? c.getParticipantIds() : null;
//                    return ChannelResponseDto.from(c, lastMessageAt, participantIds);
//                })
//                .collect(Collectors.toList());
//    }

    @Transactional
    @Override
    public void updateChannel(UUID uuid, ChannelUpdateRequestDto channelUpdateRequestDto) {
        Channel channel = channelRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("수정할 채널이 없습니다."));
        if(channel.getType() == PRIVATE){
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.setUpdate(channelUpdateRequestDto.getNewName(), channelUpdateRequestDto.getNewDescription());
    }

    @Transactional
    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channelRepository.findById(channelId).orElse(null);
        if(channel == null) return;

        channelRepository.delete(channel);
    }
}
