package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Transactional
    @Override
    public UserStatusResponseDto create(UserStatusCreateRequestDto userStatusCreateRequestDto) {
        User user = userRepository.findById(userStatusCreateRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<UserStatus> existing = userStatusRepository.findByUser_Id(user.getId());
        if (existing.isPresent()) {
            throw new RuntimeException("이미 UserStatus가 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);
        return UserStatusResponseDto.from(userStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public UserStatusResponseDto findById(UUID uuid) {
        UserStatus userStatus = userStatusRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("해당 UserStatus를 찾을 수 없습니다."));
        return UserStatusResponseDto.from(userStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStatusResponseDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(UserStatusResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserStatusResponseDto update(UUID uuid, UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(s -> s.getId().equals(uuid))
                .findFirst().orElseThrow(() -> new RuntimeException("해당 UserStatus를 찾을 수 없습니다."));

        if (userStatusUpdateRequestDto.getLastActiveAt() != null) {
            userStatus.updateLastActiveAt(userStatusUpdateRequestDto.getLastActiveAt());
        }
        return UserStatusResponseDto.from(userStatus);
    }

    @Transactional
    @Override
    public UserStatusResponseDto updateByUserId(UUID userId, UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(()->new IllegalArgumentException("User를 찾을 수 없습니다."));
        if (userStatus == null) {
            throw new RuntimeException("해당 유저의 상태를 찾을 수 없습니다");
        }
        if (userStatusUpdateRequestDto.getLastActiveAt() != null) {
            userStatus.updateLastActiveAt(userStatusUpdateRequestDto.getLastActiveAt());
        }
        return UserStatusResponseDto.from(userStatus);
    }

    @Transactional
    @Override
    public void delete(UUID uuid) {
        userStatusRepository.deleteById(uuid);
    }
}
