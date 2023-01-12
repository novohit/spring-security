package com.example.speldemo;

import org.springframework.stereotype.Component;

/**
 * @author zwx
 * @date 2023-01-10 22:33
 */
@Component("bd")
public class BeanDemo {
    public String hello() {
        System.out.println("这是一个bean");
        return "bean";
    }
}
