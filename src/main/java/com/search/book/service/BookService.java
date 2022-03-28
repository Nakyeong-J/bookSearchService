package com.search.book.service;

import com.search.book.entity.SearchHistory;
import com.search.book.model.response.BookInfo;
import com.search.book.repository.SearchHistoryRepository;
import com.search.book.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookService {

    @Autowired
    WebClient webClient;

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Mono<BookInfo> searchBook(String keyword, int searchPage, boolean historyYn, String userId) {
        if (historyYn) this.saveSearchHistory(keyword, userId);

        return webClient.get()
                .uri(baseUrl -> baseUrl.queryParam("query", keyword).queryParam("page", searchPage).build())
                .retrieve()
                .bodyToMono(BookInfo.class);

    }

    public void saveSearchHistory(String keyword, String userId) {
        log.info("Timestamp : {}", Timestamp.valueOf(LocalDateTime.now()).toString());
        searchHistoryRepository.save(SearchHistory.builder()
                .keyword(keyword)
                .regdt(Timestamp.valueOf(LocalDateTime.now()))
                .user(userRepository.findOneByUserId(userId).get())
                .build());
    }

    public List<SearchHistory> getHistoryByUser(String userId) {
        return searchHistoryRepository.findAllByUser(userRepository.findOneByUserId(userId).get());
    }

    public List<Map<String, Object>> getTopRankKeyword() {
        return searchHistoryRepository.selectTopRankKeyword();
    }
}
