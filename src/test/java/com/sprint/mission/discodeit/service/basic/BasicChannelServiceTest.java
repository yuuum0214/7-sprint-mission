package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotUpdateException;
import com.sprint.mission.discodeit.exception.channel.ChannelParticipantsException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private BasicChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void Created_Public_Channel_Success() {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelPublicCreateRequestDto requestDto
                = new ChannelPublicCreateRequestDto("채널1", "채널1입니다.");
        Channel channel = new Channel("채널1", ChannelType.PUBLIC, "채널1입니다.");
        ChannelResponseDto responseDto = ChannelResponseDto.builder()
                .id(channelId)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .build();

        when(channelRepository.save(any(Channel.class))).thenReturn(channel);
        when(channelMapper.toDto(any(Channel.class))).thenReturn(responseDto);

        // when
        ChannelResponseDto result = channelService.createPublicChannel(requestDto);

        // then
        assertThat(result.getName()).isEqualTo("채널1");
        verify(channelRepository, times(1)).save(any(Channel.class));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void Created_Private_Channel_Success() {
        // given
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        User user1 = new User("user111!", "user1@gmail.com", "user1", null);
        User user2 = new User("user222!", "user2@gmail.com", "user2", null);
        UserResponseDto user1Dto = UserResponseDto.builder()
                .id(user1Id)
                .email(user1.getEmail())
                .username(user1.getUsername())
                .online(true)
                .build();
        UserResponseDto user2Dto = UserResponseDto.builder()
                .id(user2Id)
                .email(user2.getEmail())
                .username(user2.getUsername())
                .online(true)
                .build();

        List<UUID> participantIds = List.of(user1Dto.getId(), user2Dto.getId());
        ChannelPrivateCreateRequestDto requestDto = ChannelPrivateCreateRequestDto.builder()
                .participantIds(participantIds)
                .build();
        Channel channel = new Channel(ChannelType.PRIVATE);
        ChannelResponseDto responseDto = ChannelResponseDto.builder()
                .id(channelId)
                .build();

        when(userRepository.findById(user1Dto.getId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Dto.getId())).thenReturn(Optional.of(user2));
        when(channelRepository.save(any(Channel.class))).thenReturn(channel);
        when(channelMapper.toDto(any(Channel.class))).thenReturn(responseDto);

        // when
        ChannelResponseDto result = channelService.createPrivateChannel(requestDto);

        // then
        assertThat(result).isNotNull();
        verify(readStatusRepository, times(2)).save(any(ReadStatus.class));
        verify(channelRepository, times(1)).save(any(Channel.class));


    }

    @Test
    @DisplayName("공개 채널 생성 실패 - 이름이 공백")
    void Created_Public_Channel_Fail() {
        // given
        ChannelPublicCreateRequestDto requestDto = new ChannelPublicCreateRequestDto(null, "설명");

        // when & then
        assertThatThrownBy((() -> channelService.createPublicChannel(requestDto)))
                .isInstanceOf(ChannelNotFoundException.class)
                .hasMessageContaining(ErrorCode.CHANNEL_NAME_NOT_BLANK.getMessage());

    }

    @Test
    @DisplayName("비공개 채널 생성 실패 - 참여 인원 부족")
    void Created_Private_Channel_Fail() {
        // given
        List<UUID> participantIds = List.of(UUID.randomUUID());
        ChannelPrivateCreateRequestDto requestDto
                = ChannelPrivateCreateRequestDto.builder()
                .participantIds(participantIds)
                .build();

        // when & then
        assertThatThrownBy(() -> channelService.createPrivateChannel(requestDto))
                .isInstanceOf(ChannelParticipantsException.class)
                .hasMessageContaining(ErrorCode.CHANNEL_PRIVATE_PARTICIPANTS.getMessage());
        verify(readStatusRepository, never()).save(any(ReadStatus.class));

    }

    @Test
    @DisplayName("채널명 업데이트 성공")
    void Updated_Public_Channel_Success() {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequestDto requestDto
                = ChannelUpdateRequestDto.builder()
                .newName("채널명수정")
                .newDescription("채널 이름을 바꿨답니다.")
                .build();
        Channel channel = new Channel("채널명", ChannelType.PUBLIC, "기존채널");

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        ChannelResponseDto responseDto = ChannelResponseDto.builder()
                .id(channelId)
                .name(requestDto.getNewName())
                .description(requestDto.getNewDescription())
                .build();
        when(channelMapper.toDto(any(Channel.class))).thenReturn(responseDto);

        // when
        channelService.updateChannel(channelId, requestDto);

        // then
        assertThat(channel.getName()).isEqualTo(requestDto.getNewName());
        assertThat(channel.getName()).isEqualTo("채널명수정");
        assertThat(channel.getDescription()).isEqualTo(requestDto.getNewDescription());
    }

    @Test
    @DisplayName("채널명 업데이트 실패 - 1. 채널을 찾을 수 없음")
    void Updated_Public_Channel_Fail() {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequestDto requestDto =
                ChannelUpdateRequestDto.builder()
                        .newName("이름변경")
                        .newDescription("채널이름변경")
                        .build();

        when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(()->channelService.updateChannel(channelId, requestDto))
                .isInstanceOf(ChannelNotFoundException.class)
                .hasMessageContaining(ErrorCode.CHANNEL_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("채널명 업데이트 실패 - 2. 비공개 채널 수정 불가")
    void Updated_Private_Channel_Fail() {
        // given
        UUID  channelId = UUID.randomUUID();
        ChannelUpdateRequestDto requestDto
                = ChannelUpdateRequestDto.builder()
                .newName("이름변경")
                .newDescription("이름을 변경함")
                .build();

        Channel privateChannel = new Channel(ChannelType.PRIVATE);
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(privateChannel));

        // when & then
        assertThatThrownBy(()->channelService.updateChannel(channelId, requestDto))
                .isInstanceOf(ChannelNotUpdateException.class)
                .hasMessageContaining(ErrorCode.PRIVATE_CHANNEL_NOT_IMPOSABLE_UPDATED.getMessage());
        assertThat(privateChannel.getName()).isNotEqualTo("이름변경");
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void delete_Channel_Success() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel("삭제할 채널", ChannelType.PUBLIC, "채널삭제 테스트");

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));

        // when
        channelService.deleteChannel(channelId);

        // then
        verify(channelRepository, times(1)).delete(channel);
    }

    //findByUserId를 테스트하라는 건 뭐지? 나는 그 메서드가 없는데.
}