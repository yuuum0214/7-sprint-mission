package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentBadRequestException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
        if (userRepository.existsByEmail(userCreateRequest.getEmail())) {
            log.error("ExistEmail : {}", userCreateRequest.getEmail());
            throw new UserAlreadyExistException(ErrorCode.USER_EXISTS_EMAIL);
        }
        if (userRepository.existsByUsername(userCreateRequest.getUsername())) {
            log.error("ExistUsername : {}", userCreateRequest.getUsername());
            throw new UserAlreadyExistException(ErrorCode.USER_EXISTS_NAME);
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
                log.error("BinaryError : {}", file.getOriginalFilename());
                throw new BinaryContentBadRequestException(ErrorCode.BINARY_CONTENT_UPLOAD_ERROR);
            }
        }

        User savedUser = userRepository.save(user);

        // 상태 생성, 저장
        UserStatus userStatus = new UserStatus(savedUser);
        savedUser.changeStatus(userStatus);

        userStatusRepository.save(userStatus);

        log.info("Created User = {}", userCreateRequest.getUsername());
        log.info("Created UserStatus = {}", savedUser.getUserStatus().isOnline());

        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

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
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        if (userUpdateRequestDto.getNewUsername() != null && !userUpdateRequestDto.getNewUsername().isBlank()) {
            user.setUserName(userUpdateRequestDto.getNewUsername());
            log.info("Update Username = {}",  userUpdateRequestDto.getNewUsername());
        }
        if (userUpdateRequestDto.getNewEmail() != null && !userUpdateRequestDto.getNewEmail().isBlank()) {
            user.setEmail(userUpdateRequestDto.getNewEmail());
            log.info("Update Email = {}",  userUpdateRequestDto.getNewEmail());
        }
        if (userUpdateRequestDto.getNewPassword() != null && !userUpdateRequestDto.getNewPassword().isBlank()) {
            user.setPassword(userUpdateRequestDto.getNewPassword());
            log.info("Update password is Success.");
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
                log.error("BinaryError : {}", profile.getOriginalFilename());
                throw new BinaryContentBadRequestException(ErrorCode.BINARY_CONTENT_UPLOAD_ERROR);
            }
        }
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Transactional
    @Override
    public void deleteUser(UUID uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        log.info("Deleted Username = {}, UserStatus = {}, UserProfile = {}",
                user.getUsername(), user.getUserStatus(), user.getProfile());

        userRepository.delete(user);
    }
}
