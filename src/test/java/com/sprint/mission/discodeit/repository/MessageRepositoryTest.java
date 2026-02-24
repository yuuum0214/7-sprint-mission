package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeAll
    static void setup() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    @DisplayName("채널별 메시지 페이징 조회 - 성공")
    void findAllByChannelId_Success() {
        // given
        User author = em.persist(new User("pass123!", "user@test.com", "user", null));
        Channel channel = em.persist(new Channel("publicChannel", ChannelType.PUBLIC, "public"));

        for (int i = 0; i < 11; i++) {
            em.persist(new Message(channel, author, "content " + i, null));
        }
        em.flush();
        em.clear();

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id")));

        // when
        Slice<Message> slice = messageRepository.findAllByChannelId(channel.getId(), pageable);

        // then
        assertThat(slice.getContent()).hasSize(10);
        assertThat(slice.hasNext()).isTrue();
        assertThat(slice.getContent().get(0).getContent()).isEqualTo("content 10");
    }

    @Test
    @DisplayName("채널 내 메시지 및 파일 조회 - 성공")
    void findByChannelId_Success() {
    // given
        User author = em.persist(new User("author12!", "test1@test.com","author1", null));
        Channel channel = em.persist(new Channel("publicChannel", ChannelType.PUBLIC, "public"));
        BinaryContent file1 = em.persist(new BinaryContent("file1.png", 1L, "image/png"));
        BinaryContent file2 = em.persist(new BinaryContent("file2.pdf", 1L, "application/pdf"));

        Message message = em.persist(new Message(channel, author, "files", List.of(file1, file2)));

        em.flush();
        em.clear();

    // when
        List<Message> result = messageRepository.findByChannelId(channel.getId());

    // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAttachments()).hasSize(2);
        assertThat(result.get(0).getAttachments())
                .extracting(BinaryContent::getFileName)
                .containsExactlyInAnyOrder("file1.png", "file2.pdf");
    }

    @Test
    @DisplayName("채널의 마지막 메시지 생성 시간 조회 - 성공")
    void findLastByChannel_Success() {
    // given
        User author = em.persist(new User("password!", "test@test.com", "author", null));
        Channel channel = em.persist(new Channel("publicChannel", ChannelType.PUBLIC, "public"));

        em.persist(new Message(channel, author, "Text", null));
        em.flush();

        Message lastMessage = em.persist(new Message(channel, author, "Last", null));
        em.flush();
        em.clear();

    // when
        Optional<Instant> lastTime = messageRepository.findLastByChannel(channel);

    // then
        assertThat(lastTime).isPresent();
        assertThat(lastTime.get()).isCloseTo(lastMessage.getCreatedAt(), within(1, ChronoUnit.SECONDS));
    }
}