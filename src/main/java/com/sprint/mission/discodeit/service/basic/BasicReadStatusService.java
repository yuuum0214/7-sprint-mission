package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        ReadStatus existing = readStatusRepository.findByUserIdAndChannelId(
                readStatusCreateRequestDto.getUserId(),
                readStatusCreateRequestDto.getChannelId()
        );
        if (existing != null){
//            throw new IllegalArgumentException("ReadStatus already exists");
            existing.setUpdate(Instant.now());
            ReadStatus saved = readStatusRepository.save(existing);
            return readStatusMapper.toDto(saved);
        }

        User user = userRepository.findById(readStatusCreateRequestDto.getUserId())
                .orElseThrow(()->new IllegalArgumentException("User not found"));

        Channel channel = channelRepository.findById(readStatusCreateRequestDto.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        ReadStatus readStatus = new ReadStatus(user, channel);
        readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(readStatus);
    }

    // ReadStatus문제 이제 안 뜸, 메시지 입력하면 각 채널 ID에 맞게 DB저장됨. 근데 메시지는 한 화면에 다 보임
    // 메시지가 한 창에서 다 보여서 그런지, 다른 private에 강제 참여됨.


    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto findById(UUID uuid) {
        ReadStatus readStatus = readStatusRepository.findById(uuid)
                .orElseThrow(()->new IllegalArgumentException("ReadStatus를 찾을 수 없습니다."));
        if (readStatus == null) throw new IllegalArgumentException("ReadStatus not found");
        return readStatusMapper.toDto(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(()->new IllegalArgumentException("User not found"));
        return readStatusRepository.findByUserId(user.getId())
                .stream()
                .map(readStatusMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID id, ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("ReadStatus not found"));

        if (readStatusUpdateRequestDto.getNewLastReadAt() != null
                && readStatusUpdateRequestDto.getNewLastReadAt().isAfter(readStatus.getLastReadAt())) {

            readStatus.setUpdate(readStatusUpdateRequestDto.getNewLastReadAt());
        }
        ReadStatus saved = readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(saved);
    }

    @Transactional
    @Override
    public void delete(UUID uuid) {
        ReadStatus readStatus = readStatusRepository.findById(uuid)
                .orElseThrow(()->new IllegalArgumentException("ReadStatus not found"));
        if(readStatus == null) throw new IllegalArgumentException("ReadStatus not found");
        readStatusRepository.delete(readStatus);
    }
}
