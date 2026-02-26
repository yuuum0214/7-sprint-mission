package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
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

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;
    @MockitoBean
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("메시지 전송 성공")
    void postMapping_Success() throws Exception {
        // given
        MessageCreateRequestDto requestDto = MessageCreateRequestDto.builder()
                .authorId(UUID.randomUUID())
                .channelId(UUID.randomUUID())
                .content("안녕")
                .build();
        String json = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile multipartFile
                = new MockMultipartFile("messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8));

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .id(UUID.randomUUID())
                .content("안녕")
                .build();

        given(messageService.createMessage(any(), any())).willReturn(responseDto);

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("안녕"))
                .andDo(print());
    }

    @Test
    @DisplayName("메시지 전송 실패")
    void postMapping_Fail() throws Exception {
        // given
        MessageCreateRequestDto requestDto = MessageCreateRequestDto.builder()
                .authorId(UUID.randomUUID())
                .channelId(UUID.randomUUID())
                .content("안녕")
                .build();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(requestDto).getBytes()
        );

        given(messageService.createMessage(any(), any()))
                .willThrow(new MessageNotFoundException(ErrorCode.CHANNEL_NOT_FOUND));

        // when & then
        mockMvc.perform(multipart("/api/messages").file(multipartFile))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("메시지 수정 성공")
    void patchMapping_Success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequestDto requestDto = MessageUpdateRequestDto.builder()
                .newContent("메시지 수정")
                .build();

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .id(messageId)
                .content("메시지 수정")
                .build();

        given(messageService.updateMessage(eq(messageId), any())).willReturn(responseDto);

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("메시지 수정"));
    }

    @Test
    @DisplayName("메시지 수정 실패")
    void patchMapping_Fail() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequestDto requestDto = MessageUpdateRequestDto.builder()
                .newContent("수정된 내용")
                .build();

        given(messageService.updateMessage(eq(messageId), any()))
                .willThrow(new MessageNotFoundException(ErrorCode.MESSAGE_NOT_FOUND));

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMapping_Success() throws Exception {
        UUID messageId = UUID.randomUUID();

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());

        verify(messageService).deleteMessage(messageId);
    }

    @Test
    @DisplayName("메시지 삭제 실패")
    void deleteMapping_Fail() throws Exception {
        UUID messageId = UUID.randomUUID();
        doThrow(new MessageNotFoundException(ErrorCode.MESSAGE_NOT_FOUND))
                .when(messageService).deleteMessage(messageId);

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 목록 조회 성공")
    void getMapping_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        MessageResponseDto message1 = MessageResponseDto.builder().content("목록 조회").build();
        Slice<MessageResponseDto> slice = new SliceImpl<>(List.of(message1), PageRequest.of(0, 50), false);

        given(messageService.findChannelAllMessage(eq(channelId), any(Pageable.class)))
                .willReturn(slice);

        // when & then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("목록 조회"));
    }
}