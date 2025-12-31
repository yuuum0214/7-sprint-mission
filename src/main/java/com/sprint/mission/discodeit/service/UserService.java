package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {

    void createUser(UserCreateRequestDto userCreateRequestDto, MultipartFile file); //유저 생성

//    User readUser(UUID uuid); //특정 user 조회
    UserResponseDto findById(UUID userId); //특정 user 조회

//    List<User> readAllUser(); //모든 User조회
    List<UserResponseDto> findAllUser(); //모든 User조회

    UserResponseDto updateUser(UUID userId, UserUpdateRequestDto userUpdateRequestDto, MultipartFile profile); //User 정보 수정
    //update하나로 통일. String~을 dto로 전환
//    void updatePassword(UUID uuid, String newPassword); //User 정보 수정

    void deleteUser(UUID uuid); //UUID로 User삭제
}
