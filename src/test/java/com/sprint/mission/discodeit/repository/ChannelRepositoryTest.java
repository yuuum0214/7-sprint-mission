package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("사용자 접근 가능한 채널 목록 조회 - 성공")
    void findAllAvailableForUser_Success() {
        // given
        User me = em.persist(new User("thisis12@", "me@test.com", "me", null));
        User other = em.persist(new User("other12!", "other@test.com", "other", null));
        User guest = em.persist(new User("guest12@", "guest@test.com", "guest", null));

        Channel publicChannel = em.persist(new Channel("channel1", ChannelType.PUBLIC, "this is channel1"));

        Channel privateChannel = em.persist(new Channel(ChannelType.PRIVATE));
        em.persist(new ReadStatus(me, privateChannel));
        em.persist(new ReadStatus(other, publicChannel));

        Channel otherPrivateChannel = em.persist(new Channel(ChannelType.PRIVATE));
        em.persist(new ReadStatus(other, privateChannel));
        em.persist(new ReadStatus(guest, privateChannel));

        em.flush();
        em.clear();

        // when
        List<Channel> result = channelRepository.findAllAvailableForUser(me.getId());

        // then
        assertThat(result).hasSize(2);

        Channel privateResult = result.stream()
                .filter(c -> c.getType() == ChannelType.PRIVATE)
                .findFirst()
                .orElseThrow();

        assertThat(privateResult.getName()).isNull();

        List<UUID> channelIds = result.stream().map(Channel::getId).toList();
        assertThat(channelIds).doesNotContain(otherPrivateChannel.getId());
    }

    @Test
    @DisplayName("사용자 접근 가능 채널 목록 조회 - 실패")
    void findAllAvailableForUser_Fail() {
        // given
        User me = em.persist(new User("meme123@", "me@test.com", "me", null));
        User other1 = em.persist(new User("other12@", "other@test.com", "other", null));
        User other2 = em.persist(new User("other123@", "other2@test.com", "other2", null));

        Channel private1 = em.persist(new Channel(ChannelType.PRIVATE));
        em.persist(new ReadStatus(other1, private1));
        em.persist(new ReadStatus(other2, private1));

        Channel private2 = em.persist(new Channel(ChannelType.PRIVATE));
        em.persist(new ReadStatus(other1, private2));
        em.persist(new ReadStatus(other2, private2));

        em.flush();
        em.clear();

        // when
        List<Channel> result = channelRepository.findAllAvailableForUser(me.getId());

        // then
        assertThat(result).isEmpty();

    }

    @Test
    @DisplayName("참가자 전체 조회 - 성공")
    void findAllWithParticipants_Success() {
        // given
        User user1 = em.persist(new User("userpass!", "user1@test.com", " user1", null));
        User user2 = em.persist(new User("userpass@", "user2@test.com", "user2", null));

        em.persist(new Channel("public", ChannelType.PUBLIC, "public channel"));

        Channel privateChannel = em.persist(new Channel(ChannelType.PRIVATE));
        em.persist(new ReadStatus(user1, privateChannel));
        em.persist(new ReadStatus(user2, privateChannel));

        em.flush();
        em.clear();

        // when
        List<Channel> result = channelRepository.findAllWithParticipants();

        // then
        assertThat(result).hasSize(2);
        Channel publicResult = result.stream()
                .filter(c->c.getType() == ChannelType.PUBLIC).findFirst().get();
        assertThat(publicResult.getReadStatuses()).isEmpty();

        Channel privateResult = result.stream()
                .filter(c->c.getType() == ChannelType.PRIVATE).findFirst().get();
        assertThat(privateResult.getReadStatuses()).hasSize(2);
    }

    @Test
    @DisplayName("참가자 전체 조회 - 실패")
    void findAllWithParticipants_Fail() {
        // when
        List<Channel> result = channelRepository.findAllWithParticipants();

        // then
        assertThat(result).isEmpty();
        assertThat(result).isNotNull();

    }
}