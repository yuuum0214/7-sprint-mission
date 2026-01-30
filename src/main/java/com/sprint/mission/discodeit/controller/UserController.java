package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User")
@Slf4j
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatusService;

    //사용자 생성
    @PostMapping(consumes = "multipart/form-data")
    public void create(
            @RequestPart("userCreateRequest") UserCreateRequestDto userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        System.out.println("userCreateRequest = " + userCreateRequest.getUsername());
        userService.createUser(userCreateRequest, profile);
    }

    //전체 조회
    @GetMapping
    public List<UserResponseDto> findAll() {
        return userService.findAllUser();
    }

    //사용자 수정
    @PatchMapping(value = "/{userId}", consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDto> update(
            @Parameter(description = "수정할 User ID")
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequestDto userUpdateRequestDto,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {
        UserResponseDto updatedUser = userService.updateUser(userId, userUpdateRequestDto, profile);
        return ResponseEntity.ok(updatedUser);
    }

    //접속 상태 반영
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<?> updateStatus(
            @PathVariable UUID userId,
            @RequestBody(required = false) UserStatusUpdateRequestDto userStatusUpdateRequestDto
    ) {
        userStatusService.updateByUserId(userId, userStatusUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    //사용자 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 User ID")
            @PathVariable("userId") UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
