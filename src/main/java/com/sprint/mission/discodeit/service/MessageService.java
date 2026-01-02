package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponseDto createMessage(MessageCreateRequestDto messageCreateRequestDto,
                                     List<MultipartFile> files); //메시지 전송

    MessageResponseDto findByMessage(UUID uuid); //메시지 조회

    List<MessageResponseDto> findUserAllMessage(UUID userId); //유저 관련 전체 메시지 조회

    List<MessageResponseDto> findChannelAllMessage(UUID channeId); // 채널 관련 전체 메시지 조회

    MessageResponseDto updateMessage(UUID messageId, MessageUpdateRequestDto messageUpdateRequestDto);
//                          List<MultipartFile> files); //수정

    void deleteMessage(UUID uuid); //삭제

}
