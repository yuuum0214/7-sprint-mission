package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BasicMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private BasicMessageService messageService;

    @Test
    @DisplayName("메시지 작성 성공")
    void Created_Message_Success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID BinaryContentId = UUID.randomUUID();
        MessageCreateRequestDto requestDto =
                MessageCreateRequestDto.builder()
                        .channelId(channelId)
                        .authorId(userId)
                        .content("새 메시지")
                        .build();
        MockMultipartFile file = new MockMultipartFile("files", "test.txt", "text/plain", "hello".getBytes());
        List<MultipartFile> files = List.of(file);

        User user = new User("test123@", "test@gmail.com", "test", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        Channel channel = new Channel("채널1", ChannelType.PUBLIC, "채널1입니다.");
        BinaryContent binaryContent = new BinaryContent("files.txt", 1L, file.getContentType());
        user.changeStatus(userStatus);

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(userId)
                .username(user.getUsername())
                .email(user.getEmail())
                .online(user.getUserStatus().isOnline())
                .build();
        BinaryContentResponseDto binaryContentResponseDto = BinaryContentResponseDto.builder()
                .id(BinaryContentId)
                .fileName("files.txt")
                .contentType("text/plain")
                .build();

        MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                .id(messageId)
                .channelId(channelId)
                .author(userResponseDto)
                .content("새 메시지")
                .attachments(List.of(binaryContentResponseDto))
                .build();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(messageMapper.toDto(any(Message.class))).willReturn(messageResponseDto);

        // when
        MessageResponseDto result = messageService.createMessage(requestDto, files);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthor().getUsername()).isEqualTo("test");
        assertThat(result.getAttachments()).hasSize(1);

        verify(binaryContentStorage, times(1)).put(any(), any());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 작성 실패 - 1. 채널 없음")
    void Created_Message_Fail_Channel() {
        // given
        UUID channelId = UUID.randomUUID();
        MessageCreateRequestDto requestDto = MessageCreateRequestDto.builder()
                .channelId(channelId)
                .authorId(UUID.randomUUID())
                .content("text")
                .build();

        when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(()->messageService.createMessage(requestDto, null))
                .isInstanceOf(ChannelNotFoundException.class)
                .hasMessageContaining(ErrorCode.CHANNEL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("메시지 작성 실패 - 2. 유저 못찾음")
    void Created_Message_Fail_User() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MessageCreateRequestDto requestDto = MessageCreateRequestDto.builder()
                .channelId(channelId)
                .authorId(userId)
                .content("text")
                .build();

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(mock(Channel.class)));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(()->messageService.createMessage(requestDto, null))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void Updated_Message_Success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID BinaryContentId = UUID.randomUUID();
        MessageUpdateRequestDto messageUpdateRequestDto = MessageUpdateRequestDto.builder()
                .newContent("수정된 메시지")
                .build();

        User user = new User("test123@", "test@gmail.com", "test", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        Channel channel = new Channel("채널1", ChannelType.PUBLIC, "채널1입니다.");
        Message message = new Message(channel, user, "기존 내용", new ArrayList<>());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        user.changeStatus(userStatus);

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(userId)
                .username(user.getUsername())
                .email(user.getEmail())
                .online(user.getUserStatus().isOnline())
                .build();

        MessageResponseDto result = MessageResponseDto.builder()
                .id(messageId)
                .channelId(channelId)
                .author(userResponseDto)
                .content(messageUpdateRequestDto.getNewContent())
                .build();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(messageMapper.toDto(any(Message.class))).willReturn(result);

        // when
        messageService.updateMessage(messageId, messageUpdateRequestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthor().getUsername()).isEqualTo("test");
        assertThat(result.getContent()).isEqualTo("수정된 메시지");

        verify(messageRepository).findById(messageId);
    }

    @Test
    @DisplayName("메시지 수정 실패 - 메시지 정보 없음")
    void Updated_Message_Fail() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequestDto requestDto = MessageUpdateRequestDto.builder()
                .newContent("new Text")
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(()->messageService.updateMessage(messageId, requestDto))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessageContaining(ErrorCode.MESSAGE_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void Deleted_Message_Success() {
        // given
        UUID messageId = UUID.randomUUID();
        User user = new User("test123@", "test@gmail.com", "test", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        Channel channel = new Channel("채널1", ChannelType.PUBLIC, "채널1입니다.");

        Message message = new Message(channel, user, "content", new ArrayList<>());

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        // when
        messageService.deleteMessage(messageId);

        // then
        verify(messageRepository, times(1)).delete(message);
    }

    @Test
    @DisplayName("메시지 삭제 실패")
    void Deleted_Message_Fail() {
        // given
//        MessageNotFoundException(ErrorCode.MESSAGE_NOT_FOUND)
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(()->messageService.deleteMessage(messageId))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessageContaining(ErrorCode.MESSAGE_NOT_FOUND.getMessage());
    }

    // findByChannelId도 테스트하라는데 그게 뭐지...
}