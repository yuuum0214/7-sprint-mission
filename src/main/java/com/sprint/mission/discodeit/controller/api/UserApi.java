package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserApi {

    //사용자 생성
    @Operation(summary = "User 등록")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "*/*"
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("User with email {email} already exists")
                    )
            )
    })
     void create(UserCreateRequestDto userCreateRequestDto,
                  MultipartFile profile);

    //전체 조회

    @Operation(summary = "전체 User 목록 조회", operationId = "findAll")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User 목록 조회 성공",
                    content = @Content(
                            mediaType = "*/*"
                    )
            )
    })
    public List<UserResponseDto> findAll();

    //사용자 수정
    @Operation(summary = "User 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User 정보가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "*/*"
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("user with email {newEmail} already exists")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("User with id {userId} not found")
                    )
            )
    })
    public ResponseEntity<UserResponseDto> update(
            @Parameter(description = "수정할 User ID")
            @PathVariable UUID userId,
            @RequestPart UserUpdateRequestDto userUpdateRequestDto,
            @RequestPart(value = "profile", required = false) MultipartFile profile);

    //사용자 삭제
    @Operation(summary = "User 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("User with id {id} not found")
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 User ID")
            @PathVariable("userId") UUID userId);
}
