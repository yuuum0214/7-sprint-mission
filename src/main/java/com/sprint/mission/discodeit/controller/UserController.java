package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jakarta.websocket.server.PathParam;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User")
@Slf4j
public class UserController implements UserApi {

    private final UserService userService;

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
    @PatchMapping(value= "/{userId}", consumes = "multipart/form-data")
    public void update(
            @Parameter(description = "수정할 User ID")
            @PathVariable UUID userId, @ModelAttribute UserUpdateRequestDto userUpdateRequestDto) {
        userService.updateUser(userId, userUpdateRequestDto);
    }

    //사용자 삭제
    @DeleteMapping("/{userId}")
    public void delete(
            @Parameter(description = "삭제할 User ID")
            @PathVariable("userId") UUID userId) {
        userService.deleteUser(userId);
    }
}
