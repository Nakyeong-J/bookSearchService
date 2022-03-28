package com.search.book.service;

import com.search.book.config.JwtTokenProvider;
import com.search.book.entity.User;
import com.search.book.exception.CustomException;
import com.search.book.exception.ErrorType;
import com.search.book.model.response.common.ErrorInfo;
import com.search.book.model.response.common.ReturnData;
import com.search.book.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@ComponentScan({"org.springframework.security.crypto.password"})
@Slf4j
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public ReturnData join(Map<String, String> user) {
        Optional<User> beforeJoin = userRepository.findOneByUserId(user.get("userId"));

        try {
            beforeJoin.ifPresent((value) -> {
                throw new CustomException(ErrorType.ALREADY_EXIST_ACCOUNT_ERROR);
            });
        } catch (CustomException ce) {
            return ReturnData.builder().hasError(true).errorInfo(new ErrorInfo(ce.getErrorType())).build();
        }

        return ReturnData.builder()
                .resultData(userRepository.save(User.builder()
                        .userId(user.get("userId"))
                        .password(passwordEncoder.encode(user.get("password")))
                        .username(user.get("username"))
                        .permissions(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                        .build()).getId())
                .build();
    }

    public ReturnData login(Map<String, String> user) {

        Optional<User> userInfo = userRepository.findOneByUserId(user.get("userId"));

        try {
            userInfo.orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_ACCOUNT));

            User userValue = userInfo.get();
            if (!passwordEncoder.matches(user.get("password"), userValue.getPassword())) {
                log.warn("Wrong Password -> userId:" + user.get("userId"));
                throw new CustomException(ErrorType.PASSWORD_ERROR);
            }
        } catch (CustomException ce) {
            return ReturnData.builder().hasError(true).errorInfo(new ErrorInfo(ce.getErrorType())).build();
        }


        Authentication authentication = new UsernamePasswordAuthenticationToken(user.get("userId"), user.get("password"));

        return ReturnData.builder().resultData(jwtTokenProvider.createToken(authentication)).build();
    }
}
