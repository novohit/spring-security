package com.example.speldemo;

/**
 * @author zwx
 * @date 2023-01-10 22:28
 */
public class UserService {
    public String hello() {
        System.out.println("无参");
        return "hello";
    }

    public String hello(String name) {
        System.out.println("有参");
        return "hello " + name;
    }
}
