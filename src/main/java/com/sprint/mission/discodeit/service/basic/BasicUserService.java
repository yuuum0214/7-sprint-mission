package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {

    // 레파지토리 의존성 주입
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public UserResponseDto createUser(UserCreateRequestDto userCreateRequest, MultipartFile file) {

        //유저 생성
        if (userRepository.existsByEmail(userCreateRequest.getEmail()) ||
                userRepository.existsByUsername(userCreateRequest.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 Name 혹은 Email 입니다.");
        }

        // 유저 생성
        User user = new User(
                userCreateRequest.getPassword(),
                userCreateRequest.getEmail(),
                userCreateRequest.getUsername(),
                null
                );

        //프로필 이미지 등록(선택)
        if (file != null && !file.isEmpty()) {
            try {
                BinaryContent profile = new BinaryContent(
                        file.getOriginalFilename(),
                        file.getSize(),
                        file.getContentType()
                );
                BinaryContent saved = binaryContentRepository.save(profile);

                binaryContentStorage.put(saved.getId(), file.getBytes());

                user.changeProfile(saved);

            } catch (Exception e) {
                throw new RuntimeException("프로필 처리 중 ERROR", e);
            }
        }

        log.info("DTO.email = {}", userCreateRequest.getEmail());
        log.info("DTO.userName = {}", userCreateRequest.getUsername());

        User savedUser = userRepository.save(user);

        // 상태 생성, 저장
        UserStatus userStatus = new UserStatus(savedUser);
        savedUser.changeStatus(userStatus); // TODO: setUserState 를 리펙토링할것

        userStatusRepository.save(userStatus);

        log.info("User Created = {}", userCreateRequest.getUsername());

        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> findAllUser() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(UUID userId, UserUpdateRequestDto userUpdateRequestDto, MultipartFile profile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userUpdateRequestDto.getNewUsername() != null && !userUpdateRequestDto.getNewUsername().isBlank()) {
            user.setUserName(userUpdateRequestDto.getNewUsername());
        }
        if (userUpdateRequestDto.getNewEmail() != null && !userUpdateRequestDto.getNewEmail().isBlank()) {
            user.setEmail(userUpdateRequestDto.getNewEmail());
        }
        if (userUpdateRequestDto.getNewPassword() != null && !userUpdateRequestDto.getNewPassword().isBlank()) {
            user.setPassword(userUpdateRequestDto.getNewPassword());
        }

        // 프로필 이미지 교체
        if (profile != null && !profile.isEmpty()) {
            try {
                BinaryContent newProfile = new BinaryContent(
                        profile.getOriginalFilename(),
                        profile.getSize(),
                        profile.getContentType()
                );
                BinaryContent updatedProfile = binaryContentRepository.save(newProfile);

                binaryContentStorage.put(updatedProfile.getId(), profile.getBytes());

                user.changeProfile(updatedProfile);

            } catch (Exception e) {
                throw new RuntimeException("프로필 업로드 중 오류 발생" + e.getMessage());
            }
        }
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Transactional
    @Override
    public void deleteUser(UUID uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        System.out.println("삭제 대상 : " + user.getUsername()
                + " | Status : " + user.getUserStatus()
                + " | profile : " + user.getProfile());

        userRepository.delete(user);
    }
}
