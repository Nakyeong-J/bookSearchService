package com.search.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class CommonController {
    @GetMapping(value = "/board")
    public String main() {
        return "board";
    }

    @GetMapping("/join")
    public String join() {
        return "join";
    }

    @GetMapping({"/login", "/"})
    public String login() {
        return "login";
    }
}
