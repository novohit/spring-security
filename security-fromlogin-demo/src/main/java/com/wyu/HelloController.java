package com.wyu;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zwx
 * @date 2023-01-11 14:09
 */
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "public hello";
    }

    @GetMapping("/admin/hello")
    public String adminHello() {
        return "admin hello";
    }

    @GetMapping("/user/hello")
    public String userHello() {
        return "user hello";
    }

    @PostMapping("/toMain")
    public String toMain() {
        return "success";
    }

    @PostMapping("/toError")
    public String toError() {
        return "error";
    }
}
