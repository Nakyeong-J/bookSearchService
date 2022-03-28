package com.search.book.repository;

import com.search.book.entity.SearchHistory;
import com.search.book.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT s FROM SearchHistory s WHERE s.user=?1 ORDER BY s.regdt DESC")
    List<SearchHistory> findAllByUser(User user);

    @Query(value = "SELECT ROWNUM AS RANK, KEYWORD_RANK.* FROM" +
            "(SELECT COUNT(KEYWORD) AS CNT, KEYWORD\n" +
            "FROM SEARCH_HISTORY\n" +
            "GROUP BY KEYWORD\n" +
            "ORDER BY CNT DESC LIMIT 10) AS KEYWORD_RANK",
            nativeQuery = true)
    List<Map<String, Object>> selectTopRankKeyword();
}