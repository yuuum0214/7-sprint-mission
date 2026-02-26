package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotUpdateException;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void postMappingPublic_Success() throws Exception {
        // given
        ChannelPublicCreateRequestDto requestDto = new ChannelPublicCreateRequestDto("publicChannel", "공개 채널");
        ChannelResponseDto responseDto = ChannelResponseDto.builder()
                .id(UUID.randomUUID())
                .name("publicChannel")
                .type(ChannelType.PUBLIC)
                .build();

        given(channelService.createPublicChannel(any())).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("publicChannel"));
    }

    @Test
    @DisplayName("공개 채널 생성 실패")
    void postMappingPublic_Fail() throws Exception {
        ChannelPublicCreateRequestDto requestDto = new ChannelPublicCreateRequestDto("", "");

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void postMappingPrivate_Success() throws Exception {
        // given
        ChannelPrivateCreateRequestDto requestDto = ChannelPrivateCreateRequestDto.builder()
                .participantIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .build();
        ChannelResponseDto responseDto = ChannelResponseDto.builder()
                .id(UUID.randomUUID())
                .type(ChannelType.PRIVATE)
                .build();

        given(channelService.createPrivateChannel(any())).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("채널 목록 조회 성공")
    void getMapping_Success() throws Exception {
        // given
        ChannelResponseDto chan1 = ChannelResponseDto.builder()
                .id(UUID.randomUUID())
                .name("chan1")
                .build();
        ChannelResponseDto chan2 = ChannelResponseDto.builder()
                .id(UUID.randomUUID())
                .name("chan2")
                .build();
        given(channelService.findAll()).willReturn(Arrays.asList(chan1, chan2));

        // when & then
        mockMvc.perform(get("/api/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("chan1"))
                .andExpect(jsonPath("$[1].name").value("chan2"));
    }

    @Test
    @DisplayName("특정 사용자 채널 조회 성공")
    void getMapping_Private_Success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(channelService.findAllByUserId(userId))
                .willReturn(List.of(ChannelResponseDto.builder().type(ChannelType.PRIVATE).build()));
        ;

        // when & then
        mockMvc.perform(get("/api/channels")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("PRIVATE"));
    }

    @Test
    @DisplayName("채널 수정 성공")
    void patchMapping_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequestDto requestDto = ChannelUpdateRequestDto.builder()
                .newName("UpdateChannel").build();

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(channelService).updateChannel(eq(channelId), any());
    }

    @Test
    @DisplayName("채널 수정 실패")
    void patchMapping_Fail() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequestDto requestDto = ChannelUpdateRequestDto.builder().newName("New").build();

        doThrow(new ChannelNotUpdateException(ErrorCode.PRIVATE_CHANNEL_NOT_IMPOSABLE_UPDATED))
                .when(channelService).updateChannel(eq(channelId), any());

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteMapping_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        // when &then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());

        verify(channelService).deleteChannel(eq(channelId));
    }

    @Test
    @DisplayName("채널 삭제 실패")
    void deleteMapping_Fail() throws Exception {
    // given
        UUID channelId = UUID.randomUUID();

    // when & then
        doThrow(new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND))
                .when(channelService).deleteChannel(eq(channelId));

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNotFound());
    }
}