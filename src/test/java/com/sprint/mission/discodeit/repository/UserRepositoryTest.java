package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("username 찾기 - 성공")
    void findByUsername_Success() {
        // given
        User user = new User("test123!", "test@gmail.com", "user", null);
        userRepository.save(user);

        // when
        Optional<User> findByUsername = userRepository.findByUsername("user");

        // then
        assertThat(findByUsername).isPresent();
        assertThat(findByUsername.get().getUsername()).isEqualTo("user");

    }

    @Test
    @DisplayName("username 찾기 - 실패")
    void findByUsername_Fail() {
        // when
        Optional<User> findByUsername = userRepository.findByUsername("test");

        // then
        assertThat(findByUsername).isEmpty();

    }

    @Test
    @DisplayName("email 조회 - 성공")
    void existsByEmail_Success() {
        // given
        User user = new User("test123@", "test@gmail.com", "user", null);
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByEmail("test@gmail.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("email 조회 - 실패")
    void existsByEmail_Fail() {
        // when
        boolean exists = userRepository.existsByEmail("non@gmail.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Username 조회 - 성공")
    void existsByUsername_Success() {
        // given
        User user = new User("test123@", "test@gmail.com", "user", null);
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByUsername("user");

        // then
        assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Username 조회 - 실패")
    void existsByUsername_Fail() {
        // when
        boolean exists = userRepository.existsByUsername("metamong");

        // then
        assertThat(exists).isFalse();
    }
}