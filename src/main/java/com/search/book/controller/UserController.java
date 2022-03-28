package com.search.book.controller;

import com.search.book.model.response.common.ReturnData;
import com.search.book.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;



    @PostMapping("/join")
    public ReturnData join(@RequestBody Map<String, String> user) {
        return userService.join(user);
    }

    @PostMapping("/login")
    public ReturnData login(@RequestBody Map<String, String> user) {
        return userService.login(user);
    }
}
