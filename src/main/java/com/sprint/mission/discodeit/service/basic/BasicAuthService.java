package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequestDto;
import com.sprint.mission.discodeit.dto.response.LoginResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.AuthFailException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByUsername(loginRequestDto.getUsername()
        ).orElseThrow(() -> new AuthFailException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        if(!user.getPassword().equals(loginRequestDto.getPassword())){
            throw new AuthFailException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        user.getUserStatus().updateLastActiveAt(Instant.now());
        return LoginResponseDto.from(user);
    }
}
