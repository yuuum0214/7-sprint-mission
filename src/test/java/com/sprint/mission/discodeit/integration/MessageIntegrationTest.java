package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MessageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private EntityManager em;

    private User savedUser;
    private Channel savedChannel;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(new User("test123!", "test@test.com", "test", null));
        savedChannel = channelRepository.save(new Channel("채널1", ChannelType.PUBLIC, "채널1입니다."));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessage_Success() throws Exception {
        // given
        MessageCreateRequestDto requestDto = MessageCreateRequestDto.builder()
                .authorId(savedUser.getId())
                .channelId(savedChannel.getId())
                .content("안녕!")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                "application/json",
                jsonRequest.getBytes(StandardCharsets.UTF_8)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("안녕!"))
                .andExpect(jsonPath("$.author.username").value("test"));
    }

    @Test
    @DisplayName("메시지 내용 수정")
    void updateMessage_Success() throws Exception {
        // given
        Message message = messageRepository.save(new Message(savedChannel, savedUser, "기존 내용", null));
        em.flush();
        em.clear();

        MessageUpdateRequestDto updateRequestDto = MessageUpdateRequestDto.builder()
                .newContent("수정할 내용")
                .build();

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", message.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정할 내용"));
    }

    @Test
    @DisplayName("메시지 삭제")
    void deleteMessage_Success() throws Exception {
        // given
        Message message = messageRepository.save(new Message(savedChannel, savedUser, "삭제할 메시지", null));

        // when
        mockMvc.perform(delete("/api/messages/{messageId}", message.getId()))
                .andExpect(status().isNoContent());

        // then
        assertThat(messageRepository.findById(message.getId())).isEmpty();
    }
    
    @Test
    @DisplayName("메시지 목록 조회")
    void getMessagesByChannel_Success() throws Exception {
    // given
        messageRepository.save(new Message(savedChannel, savedUser, "메시지1", null));
        messageRepository.save(new Message(savedChannel, savedUser, "메시지2", null));
        em.flush();
        em.clear();

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", savedChannel.getId().toString())
            .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].content").exists())
            .andExpect(jsonPath("$.hasNext").exists());
    
    }

}
