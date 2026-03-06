package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ChannelIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성")
    void createPublicChannel_Success() throws Exception {
        // given
        ChannelPublicCreateRequestDto requestDto = new ChannelPublicCreateRequestDto("channel1", "This is Channel1");

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("channel1"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    @Test
    @DisplayName("공개 채널 생성 실패")
    void createPublicChannel_Fail() throws Exception {
        // given
        ChannelPublicCreateRequestDto requestDto = new ChannelPublicCreateRequestDto("", "");

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비공개 채널 생성")
    void createPrivateChannel_Success() throws Exception {
        // given
        User user1 = userRepository.save(new User("test123!", "test1@test.com", "user1", null));
        User user2 = userRepository.save(new User("test121!", "test2@test.com", "user2", null));
        em.flush();
        em.clear();
        List<UUID> participants = List.of(user1.getId(), user2.getId());
        ChannelPrivateCreateRequestDto request = ChannelPrivateCreateRequestDto.builder().participantIds(participants).build();

        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("공개 채널 정보 수정")
    void updatePublicChannel_Success() throws Exception {
        // given
        Channel channel = channelRepository.save(new Channel("기존이름", ChannelType.PUBLIC, "채널설명"));
        em.flush();
        em.clear();

        ChannelUpdateRequestDto requestDto = ChannelUpdateRequestDto.builder()
                .newName("새이름")
                .newDescription("설명 변경")
                .build();

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        Channel updated = channelRepository.findById(channel.getId()).get();
        assertThat(updated.getName()).isEqualTo("새이름");
    }

    @Test
    @DisplayName("비공개 채널 수정 실패")
    void updatePrivateChannel_Fail() throws Exception {
        // given
        Channel privateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE));
        em.flush();
        em.clear();

        ChannelUpdateRequestDto updateRequestDto = ChannelUpdateRequestDto.builder()
                .newName("수정시도")
                .build();

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", privateChannel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전체 채널 목록 조회")
    void findAllCannels_Success() throws Exception {
        // given
        channelRepository.save(new Channel("채널1", ChannelType.PUBLIC, "채널1입니다."));
        channelRepository.save(new Channel("채널2", ChannelType.PUBLIC, "채널2입니다."));

        // when & then
        mockMvc.perform(get("/api/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("특정 사용자 채널 목록 조회")
    void findChannelsByUserId_Success() throws Exception {
        // given
        User user = userRepository.save(new User("test123!", "test@test.com", "user1", null));
        User user2 = userRepository.save(new User("test122!", "test2@test.com", "user2", null));

        channelRepository.save(new Channel("채널1", ChannelType.PUBLIC, "채널1입니다."));

        ChannelPrivateCreateRequestDto  requestDto = ChannelPrivateCreateRequestDto.builder().participantIds(List.of(user.getId(), user2.getId())).build();
        channelService.createPrivateChannel(requestDto);
        em.flush();
        em.clear();

        // when & then
        mockMvc.perform(get("/api/channels")
                        .param("userId", user.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannel_Success() throws Exception {
    // given
    Channel channel = channelRepository.save(new Channel("삭제 채널", ChannelType.PUBLIC, "삭제할 채널"));
    UUID channelId = channel.getId();

    // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());

        assertThat(channelRepository.findById(channelId)).isEmpty();
    }
}
