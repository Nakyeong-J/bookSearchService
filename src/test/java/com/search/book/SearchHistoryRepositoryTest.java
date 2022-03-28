package com.search.book;

import com.search.book.entity.SearchHistory;
import com.search.book.entity.User;
import com.search.book.repository.SearchHistoryRepository;
import com.search.book.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("검색 히스토리 Repository 테스트")
public class SearchHistoryRepositoryTest {
    @Autowired
    SearchHistoryRepository searchHistoryRepository;

    @Autowired
    UserRepository userRepository;

    User requestUser;

    @AfterEach
    public void afterCleanup() {
        searchHistoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void initAccount() {
        String userId = "testId";
        String userName = "testName";
        String password = "testdPassword";

        requestUser = userRepository.save(User.builder()
                .userId(userId)
                .password(password)
                .username(userName)
                .permissions(Collections.singletonList("ROLE_USER"))
                .build());
    }

    @Test
    @DisplayName("요청 검색 키워드와 검색 타입이 DB에 저장되어야 한다.")
    public void insertTest() {
        String searchKeyword = "미움받을 용기";

        SearchHistory createdEntity = searchHistoryRepository.save(SearchHistory.builder()
                .user(requestUser)
                .keyword(searchKeyword)
                .regdt(Timestamp.valueOf(LocalDateTime.now()))
                .build());

        assertThat(createdEntity, notNullValue());
        assertThat(createdEntity.getUser(), equalTo(requestUser));
        assertThat(createdEntity.getKeyword(), equalTo(searchKeyword));
    }

    @Test
    @DisplayName("등록된 검색 히스토리 데이터를 반환해야 한다.")
    public void findRecentlyHistory() {
        List<String> searchKeywords = Arrays.asList("미움받을 용기", "용기", "미움받을", "미움");

        Stack<SearchHistory> createdStack = new Stack<>();
        searchKeywords.stream().map(keyword -> searchHistoryRepository.save(SearchHistory.builder()
                .user(requestUser)
                .keyword(keyword)
                .regdt(Timestamp.valueOf(LocalDateTime.now()))
                .build())).forEach(createdStack::push);

        List<SearchHistory> result = searchHistoryRepository.findAllByUser(requestUser);

        result.stream().forEach((history) -> {
            SearchHistory beforeCreated = createdStack.pop();
            assertThat(history.getId(), equalTo(beforeCreated.getId()));
        });
    }

    @Test
    @DisplayName("조회시 키워드 검색 횟수 만큼의 값을 반환 해야 한다.")
    public void getTopRankKeyword() {
        List<String> searchKeywords = Arrays.asList(
                "미움받을 용기", "용기", "미움받을", "미움",
                "미움받을 용기", "미움받을 용기", "미움받을 용기", "미움받을 용기",
                "미움", "미움", "미움", "미움", "미움", "용기", "용기"
                );
        searchKeywords.stream().map(keyword -> searchHistoryRepository.save(SearchHistory.builder()
                .user(requestUser)
                .keyword(keyword)
                .regdt(Timestamp.valueOf(LocalDateTime.now()))
                .build()));

        Map<String, Long> keywordCount = searchKeywords.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<Map<String, Object>> result = searchHistoryRepository.selectTopRankKeyword();

        result.forEach((a) -> {
            Long expectedCount = keywordCount.get(a.get("KEYWORD"));
            assertThat(a.get("CNT"), equalTo(expectedCount.intValue()));
        });
    }

}