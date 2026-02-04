package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequestDto;
import com.sprint.mission.discodeit.dto.response.LoginResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.AuthFailException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByUsername(loginRequestDto.getUsername()
        ).orElseThrow(() -> {
            log.warn("Fail Username");
            return new AuthFailException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        });

        if(!user.getPassword().equals(loginRequestDto.getPassword())){
            log.warn("Fail Password");
            throw new AuthFailException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        user.getUserStatus().updateLastActiveAt(Instant.now());
        return LoginResponseDto.from(user);
    }
}
