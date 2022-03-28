package com.search.book;

import com.search.book.entity.User;
import com.search.book.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("User 테스트")
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("User 데이터 생성 테스트")
    public void insertTest() {
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";

        User newUser = userRepository.save(User.builder()
                .userId(userId)
                .password(password)
                .username(userName)
                .build());

        Optional<User> user = userRepository.findOneByUserId(newUser.getUserId());

        assertThat(newUser, notNullValue());
    }

    @Test
    @DisplayName("이미 해당 userId로 등록된 계정이 존재하는 경우, 오류를 반환해야 한다.")
    public void insertDuplicateTest() {
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";

        userRepository.save(User.builder()
                .userId(userId)
                .password(password)
                .username(userName)
                .build());
        DataAccessException exception = assertThrows(DataAccessException.class, () -> userRepository.save(User.builder()
                .userId(userId)
                .password(password)
                .username(userName)
                .build()));

        assertThat(exception, notNullValue());
    }

    @Test
    @DisplayName("아이디 조회 테스트")
    public void findOneByUserIdTest() {
        // Given
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";

        userRepository.save(User.builder()
                .userId(userId)
                .password(password)
                .username(userName)
                .build());

        // When
        Optional<User> findUser = userRepository.findOneByUserId(userId);

        // Then
        assertThat(findUser.isPresent(), equalTo(true));
        assertThat(findUser.get().getUserId(), equalTo(userId));
    }

}
