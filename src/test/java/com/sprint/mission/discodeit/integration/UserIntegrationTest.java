package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 생성 성공")
    void createUser_Success() throws Exception {
        // given
        UserCreateRequestDto request = new UserCreateRequestDto("user1", "user@test.com", "test123!");
        String content = objectMapper.writeValueAsString(request);

        MockMultipartFile userRequest = new MockMultipartFile(
                "userCreateRequest",
                "",
                "application/json",
                content.getBytes(StandardCharsets.UTF_8)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(userRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        assertThat(userRepository.findByUsername("user1")).isPresent();
    }

    @Test
    @DisplayName("사용자 전체 목록 조회")
    void findAllUsers_Success() throws Exception {
        // given
        userRepository.save(new User("test123!", "user1@test.com", "user1", null));
        userRepository.save(new User("test111!", "user2@test.com", "user2", null));

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").exists());
    }

    @Test
    @DisplayName("사용자 정보 수정")
    void updateUser() throws Exception {
        // given
        User savedUser = userRepository.save(new User("oldPassword", "old@mail.com", "olduser", null));
        UserUpdateRequestDto updateRequestDto = new UserUpdateRequestDto("newName", "new@mail.com", "newPassword!");
        String content = objectMapper.writeValueAsString(updateRequestDto);

        MockMultipartFile updatePart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                "application/json",
                content.getBytes(StandardCharsets.UTF_8)
        );

        // when & then
        mockMvc.perform(multipart("/api/users/" + savedUser.getId())
                        .file(updatePart)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newName"));
    }

    @Test
    @DisplayName("사용자 접속 상태 수정")
    void updateStatus_Success() throws Exception {
        // given
        User user = new User("test123!", "test@test.com", "user1", null);

        UserStatus status = new UserStatus(user);
        user.changeStatus(status);

        userRepository.save(user);

        em.flush();
        em.clear();

        UUID savedId = user.getId();

        UserStatusUpdateRequestDto statusRequest = new UserStatusUpdateRequestDto(Instant.now());
        String content = objectMapper.writeValueAsString(statusRequest);

        // when & then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteUser_Success() throws Exception {
        // given
        User savedUser = userRepository.save(new User("test123!", "test@test.com", "user1", null));
        UUID userId = savedUser.getId();

        // when
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent());

        // then
        assertThat(userRepository.findById(userId)).isEmpty();
    }
}
