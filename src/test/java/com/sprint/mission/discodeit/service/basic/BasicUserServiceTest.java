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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("유저 생성 성공")
    void Created_User_Success() {
        // given
        UserCreateRequestDto requestDto
                = new UserCreateRequestDto("test", "test@gmail.com", "test123!!");
        MockMultipartFile file
                = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        UUID userId = UUID.randomUUID();
        User user = new User("test123!", "test@gmail.com", "test", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        user.changeStatus(userStatus);

        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(binaryContentRepository.save(any())).willReturn(new BinaryContent("test.png", 10L, "image/png"));
        UserResponseDto expectResponse = UserResponseDto.builder()
                .id(userId)
                .username(user.getUsername())
                .email(user.getEmail())
                .online(user.getUserStatus().isOnline())
                .build();
        given(userRepository.save(any())).willReturn(user);
        given(userStatusRepository.save(any())).willReturn(userStatus);
        given(userMapper.toDto(any())).willReturn(expectResponse);

        // when
        UserResponseDto response = userService.createUser(requestDto, file);

        // then
        assertThat(response.getUsername()).isEqualTo("test");
        verify(userRepository).save(any(User.class));
        verify(binaryContentRepository).save(any(BinaryContent.class));
        verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("유저 생성 실패 - 1. 이메일 중복")
    void Created_User_Fail_Email() {
        // given
        UserCreateRequestDto requestDto = new UserCreateRequestDto("test", "test@gmail.com", "test123!");
        given(userRepository.existsByEmail("test@gmail.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(requestDto, null))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining(ErrorCode.USER_EXISTS_EMAIL.getMessage());
    }

    @Test
    @DisplayName("유저 생성 실패 - 2. 유저 이름 중복")
    void Created_User_Fail_Username() {
        // given
        UserCreateRequestDto requestDto = new UserCreateRequestDto("test", "test@gmail.com", "test123!");
        given(userRepository.existsByUsername("test")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(requestDto, null))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining(ErrorCode.USER_EXISTS_NAME.getMessage());
    }

    @Test
    @DisplayName("유저 생성 실패 - 3. 파일 업로드 실패")
    void Created_User_Fail_BinaryContent() {
        // given
        UserCreateRequestDto requestDto = new UserCreateRequestDto("test", "test@gmail.com", "test123!");
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(binaryContentRepository.save(any())).willReturn(new BinaryContent("test.png", 10L, "image/png"));

        // when
        lenient().doThrow(new RuntimeException()).when(binaryContentStorage).put(any(), any());

        // then
        assertThatThrownBy(() -> userService.createUser(requestDto, file))
                .isInstanceOf(BinaryContentBadRequestException.class)
                .hasMessageContaining(ErrorCode.BINARY_CONTENT_UPLOAD_ERROR.getMessage());

    }


    @Test
    @DisplayName("유저 수정 성공")
    void Updated_User_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("test123!", "test@gmail.com", "test", null);
        MockMultipartFile file
                = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        UserUpdateRequestDto requestDto =
                UserUpdateRequestDto.builder()
                        .newUsername("okay")
                        .newPassword(null)
                        .newEmail("test123@gmail.com")
                        .build();
        UserResponseDto responseDto = UserResponseDto.builder()
                .id(userId)
                .username(user.getUsername())
                .build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(binaryContentRepository.save(any())).willReturn(new BinaryContent("test.png", 10L, "image/png"));
        given(userRepository.save(any())).willReturn(user);
        given(userMapper.toDto(any())).willReturn(responseDto);

        // when
        userService.updateUser(userId, requestDto, file);

        // then
        assertThat(user.getUsername()).isEqualTo("okay");
        assertThat(user.getEmail()).isEqualTo("test123@gmail.com");
    }

    @Test
    @DisplayName("유저 수정 실패 - 1. 존재하지 않는 유저 ")
    void Updated_User_Fail_User() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                .newUsername(null)
                .newPassword(null)
                .newEmail(null)
                .build();

        // when & then
        assertThatThrownBy(() -> userService.updateUser(userId, requestDto, null))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("유저 수정 실패 - 2. 파일 업로드 실패")
    void Updated_User_Fail_() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("test123!", "test@gmail.com", "test", null);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        given(binaryContentRepository.save(any())).willReturn(new BinaryContent("test.png", 10L, "image/png"));

        doThrow(new RuntimeException()).when(binaryContentStorage).put(any(UUID.class), any(byte[].class));
        UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder().build();

        // when & then
        assertThatThrownBy(() -> userService.updateUser(userId, requestDto, file))
                .isInstanceOf(BinaryContentBadRequestException.class)
                .hasMessageContaining(ErrorCode.BINARY_CONTENT_UPLOAD_ERROR.getMessage());
        verify(binaryContentStorage, times(1)).put(any(), any());
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void Deleted_User_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("test123!", "test@gmail.com", "test", null);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.deleteUser(userId);

        // then
        verify(userRepository).delete(user);

    }

    @Test
    @DisplayName("유저 삭제 실패")
    void Deleted_User_Fail() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

    }
}