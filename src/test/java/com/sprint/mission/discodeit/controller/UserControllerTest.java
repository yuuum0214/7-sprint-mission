package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserStatusService userStatusService;

    @Test
    @DisplayName("사용자 생성 성공 - MultipartFile포함")
    void postMapping_Success() throws Exception {
        // given
        UserCreateRequestDto requestDto = new UserCreateRequestDto("user1", "user@test.com", "pass123!");
        String dtoJson = objectMapper.writeValueAsString(requestDto);

        MockMultipartFile userPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                dtoJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "test".getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(userPart)
                        .file(profilePart))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).createUser(any(UserCreateRequestDto.class), any());
    }

    @Test
    @DisplayName("사용자 생성 실패 - 이메일 존재")
    void postMapping_Fail_ExistsEmail() throws Exception {
        // given
        UserCreateRequestDto requestDto = new UserCreateRequestDto("user", "user@test.com", "pass123!");
        String dtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile userPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                dtoJson.getBytes(StandardCharsets.UTF_8)
        );

        doThrow(new UserAlreadyExistException(ErrorCode.USER_EXISTS_EMAIL))
                .when(userService).createUser(any(UserCreateRequestDto.class), any());

        // when & then
        mockMvc.perform(multipart("/api/users").file(userPart))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    @DisplayName("전체 사용자 조회 성공")
    void getMapping_Success() throws Exception {
        // given
        UserResponseDto user1 = new UserResponseDto(
                UUID.randomUUID(),
                "User1",
                "user1@test.com",
                null,
                true
        );
        UserResponseDto user2 = new UserResponseDto(
                UUID.randomUUID(),
                "User2",
                "user2@test.com",
                null,
                true
        );
        given(userService.findAllUser()).willReturn(List.of(user1, user2));

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("User1"))
                .andExpect(jsonPath("$[1].username").value("User2"))
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void patchMapping_Success() throws Exception {
        // given
        UserUpdateRequestDto updateRequestDto
                = UserUpdateRequestDto.builder()
                .newUsername("NewName")
                .build();
        String updateJson = objectMapper.writeValueAsString(updateRequestDto);

        MockMultipartFile updatePart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                updateJson.getBytes()
        );
        UUID userId = UUID.randomUUID();
        UserResponseDto responseDto = new UserResponseDto(userId, "NewName", null, null, true);
        given(userService.updateUser(eq(userId), any(), any())).willReturn(responseDto);

        // when & then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
                        .file(updatePart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("NewName"))
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 수정 실패 - 존재하지 않는 사용자")
    void patchMapping_fail_NotFoundUser() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequestDto updateRequestDto = UserUpdateRequestDto.builder()
                .newUsername("NewName")
                .build();
        String json = objectMapper.writeValueAsString(updateRequestDto);
        MockMultipartFile updatePart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes()
        );

        given(userService.updateUser(eq(userId), any(), any()))
                .willThrow(new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
                .file(updatePart))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 수정 실패 - 중복된 이름")
    void patchMapping_Fail_ExistsUser() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequestDto updateRequestDto = UserUpdateRequestDto.builder()
            .newUsername("AlreadyUser")
            .build();
    String json = objectMapper.writeValueAsString(updateRequestDto);
    MockMultipartFile updatePart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            json.getBytes()
    );

    given(userService.updateUser(eq(userId), any(), any()))
            .willThrow(new UserAlreadyExistException(ErrorCode.USER_EXISTS_NAME));

    // when & then
    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", userId).file(updatePart))
            .andExpect(status().isConflict())
            .andDo(print());
    }

    @Test
    @DisplayName("접속 상태 업데이트")
    void updateStatus() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserStatusUpdateRequestDto statusUpdateRequestDto = new UserStatusUpdateRequestDto(Instant.now());
        String content = objectMapper.writeValueAsString(statusUpdateRequestDto);

        // when & then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(print());
        verify(userStatusService).updateByUserId(eq(userId), any());
    }

    @Test
    @DisplayName("삭제 성공")
    void delete_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(userService).deleteUser(userId);
    }

    @Test
    @DisplayName("삭제 실패")
    void delete_Fail() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    doThrow(new UserNotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userService).deleteUser(userId);

    // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}