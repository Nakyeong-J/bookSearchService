package com.search.book;

import com.search.book.config.JwtTokenProvider;
import com.search.book.entity.User;
import com.search.book.model.response.common.ReturnData;
import com.search.book.repository.UserRepository;
import com.search.book.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("계정 서비스 테스트")
public class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @AfterEach
    public void afterEach() {
        reset(userRepository, passwordEncoder, jwtTokenProvider);
    }


    @Test
    @DisplayName("가입시 요청 데이터가 DB에 데이터가 생성되어야 한다.")
    public void createAccountIntoDatabase() {
        // Given
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";
        String expectedEncPassword = "encodePassword";

        Map<String, String> requestUser = new HashMap<>();
        requestUser.put("userId", userId);
        requestUser.put("userName", userName);
        requestUser.put("password", password);

        when(passwordEncoder.encode(password))
                .thenReturn(expectedEncPassword);
        when(userRepository.save(any()))
                .thenReturn(User.builder()
                        .id(1L)
                        .userId(userId)
                        .password(password)
                        .username(userName)
                        .build());

        userService.join(requestUser);

        // Then
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argument.capture());

        User callArgument = argument.getValue();
        assertThat(callArgument.getUserId(), equalTo(userId));
        assertThat(callArgument.getPassword(), equalTo(expectedEncPassword));
    }

    @Test
    @DisplayName("가입시 사용자 ID 중복 체크가 되어야 한다.")
    public void checkDuplicateUserId() {
        // Given
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";

        Map<String, String> requestUser = new HashMap<>();
        requestUser.put("userId", userId);
        requestUser.put("userName", userName);
        requestUser.put("password", password);

        when(userRepository.findOneByUserId(userId))
                .thenReturn(Optional.empty());
        when(userRepository.save(any()))
                .thenReturn(User.builder()
                        .id(1L)
                        .userId(userId)
                        .password(password)
                        .username(userName)
                        .build());

        userService.join(requestUser);

        // Then
        verify(userRepository).findOneByUserId(userId);
    }

    @Test
    @DisplayName("가입시 이미 동일한 계정 아이디가 존재하는 경우 예외가 발생해야 한다.")
    public void existSameUserIdThrowException() {
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";

        Map<String, String> requestUser = new HashMap<>();
        requestUser.put("userId", userId);
        requestUser.put("userName", userName);
        requestUser.put("password", password);

        when(userRepository.findOneByUserId(userId))
                .thenReturn(Optional.of(new User()));

        ReturnData result = userService.join(requestUser);

        assertThat(result.isHasError(), equalTo(true));
        assertThat(result.getErrorInfo().getCode(), equalTo(HttpStatus.BAD_REQUEST.value()));
        assertThat(result.getErrorInfo().getMessage(), equalTo("이미 존재하는 ID 입니다."));
    }

    @Test
    @DisplayName("가입시 비밀번호가 암호화되어야 한다.")
    public void shouldRequestPasswordEncoding() {
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";

        Map<String, String> requestUser = new HashMap<>();
        requestUser.put("userId", userId);
        requestUser.put("userName", userName);
        requestUser.put("password", password);

        when(userRepository.findOneByUserId(userId))
                .thenReturn(Optional.empty());
        when(userRepository.save(any()))
                .thenReturn(User.builder()
                        .id(1L)
                        .userId(userId)
                        .password(password)
                        .username(userName)
                        .build());

        when(passwordEncoder.encode(password))
                .thenReturn("암호화됬당!");

        userService.join(requestUser);

        verify(passwordEncoder).encode(password);
    }

    @Test
    @DisplayName("로그인시 요청 아이디에 해당하는 계정 데이터를 조회해야 한다.")
    public void findAccountWithRequestUserId() {
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";

        Map<String, String> requestUser = new HashMap<>();
        requestUser.put("userId", userId);
        requestUser.put("password", password);

        when(userRepository.findOneByUserId(userId))
                .thenReturn(Optional.of(User.builder()
                        .id(Long.valueOf("1"))
                        .userId(userId)
                        .password(password)
                        .username(userName)
                        .build()));
        when(jwtTokenProvider.createToken(any()))
                .thenReturn("JWT Token");
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        userService.login(requestUser);

        verify(userRepository).findOneByUserId(userId);
    }

    @Test
    @DisplayName("로그인시 계정 데이터가 존재하지 않는 경우, 오류를 반환해야 한다.")
    public void notFoundAccountThrowError() {
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";

        Map<String, String> requestUser = new HashMap<>();
        requestUser.put("userId", userId);
        requestUser.put("password", password);

        when(userRepository.findOneByUserId(userId))
                .thenReturn(Optional.empty());

        ReturnData result = userService.login(requestUser);

        assertThat(result.isHasError(), equalTo(true));
        assertThat(result.getErrorInfo().getCode(), equalTo(HttpStatus.NOT_FOUND.value()));
        assertThat(result.getErrorInfo().getMessage(), equalTo("회원정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("로그인시 요청 비밀번호가 유효한지 검증해야 한다.")
    public void checkPasswordMatches() {
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";
        String encodedPassword = "encodedPassword";

        Map<String, String> requestUser = new HashMap<>();
        requestUser.put("userId", userId);
        requestUser.put("password", password);

        when(userRepository.findOneByUserId(userId))
                .thenReturn(Optional.of(User.builder()
                        .id(Long.valueOf("1"))
                        .userId(userId)
                        .password(encodedPassword)
                        .username(userName)
                        .build()));
        when(jwtTokenProvider.createToken(any()))
                .thenReturn("JWT Token");
        when(passwordEncoder.matches(password, encodedPassword))
                .thenReturn(true);

        userService.login(requestUser);

        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    @DisplayName("로그인시 요청 비밀번호가 일치하지 않는 경우, 오류를 반환해야 한다.")
    public void checkPasswordNotMatchThrowError() {
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";
        String encodedPassword = "encodedPassword";

        Map<String, String> requestUser = new HashMap<>();
        requestUser.put("userId", userId);
        requestUser.put("password", password);

        when(userRepository.findOneByUserId(userId))
                .thenReturn(Optional.of(User.builder()
                        .id(Long.valueOf("1"))
                        .userId(userId)
                        .password(encodedPassword)
                        .username(userName)
                        .build()));
        when(passwordEncoder.matches(password, encodedPassword))
                .thenReturn(false);

        ReturnData result = userService.login(requestUser);

        assertThat(result.isHasError(), equalTo(true));
        assertThat(result.getErrorInfo().getCode(), equalTo(HttpStatus.NOT_EXTENDED.value()));
        assertThat(result.getErrorInfo().getMessage(), equalTo("비밀번호가 틀립니다."));
    }

    @Test
    @DisplayName("로그인시 발급된 JWT 토큰을 전달해야 한다.")
    public void returnCreateJWTToken() {
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";
        String encodedPassword = "encodedPassword";

        Map<String, String> requestUser = new HashMap<>();
        requestUser.put("userId", userId);
        requestUser.put("password", password);

        String expectedJwtToken = "JWT Token";

        when(userRepository.findOneByUserId(userId))
                .thenReturn(Optional.of(User.builder()
                        .id(Long.valueOf("1"))
                        .userId(userId)
                        .password(encodedPassword)
                        .username(userName)
                        .build()));
        when(jwtTokenProvider.createToken(any()))
                .thenReturn(expectedJwtToken);
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        ReturnData result = userService.login(requestUser);

        assertThat(result, is(notNullValue()));
        assertThat((String)result.getResultData(), equalTo(expectedJwtToken));
    }

}
