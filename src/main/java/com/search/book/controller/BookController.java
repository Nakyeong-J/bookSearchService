package com.search.book.controller;

import com.search.book.entity.SearchHistory;
import com.search.book.model.response.BookInfo;
import com.search.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
public class BookController {

    @Autowired
    BookService bookService;

    @GetMapping("/searchBook")
    public Mono<BookInfo> searchBook(Principal principal,
                                     @RequestParam(name = "searchKeyword") String searchKeyword,
                                     @RequestParam(name = "page", defaultValue = "1") int searchPageNumber,
                                     @RequestParam(defaultValue = "false") boolean historyYn
    ) {

        log.info("userInfo : {} ", principal.getName());
        log.info("searchKeyword : {} , searchPageNumber : {}, historyYn : {}", searchKeyword, searchPageNumber, historyYn);

        return bookService.searchBook(searchKeyword, searchPageNumber, historyYn, principal.getName());
    }

    @GetMapping("/getHistoryByUser")
    public List<SearchHistory> getHistoryByUser(Principal principal) {
        return bookService.getHistoryByUser(principal.getName());
    }

    @GetMapping(value = "/getTopRankKeyword")
    public List<Map<String, Object>> getTopRankKeyword() {
        return bookService.getTopRankKeyword();
    }

}
