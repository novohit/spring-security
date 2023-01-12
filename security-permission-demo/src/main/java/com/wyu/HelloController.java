package com.wyu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zwx
 * @date 2023-01-12 15:55
 */
@RestController
public class HelloController {
    @GetMapping("/hello")
    public User hello() {
        SecurityContext context = SecurityContextHolder.getContext();
        User user = (User) context.getAuthentication().getPrincipal();
        for (GrantedAuthority authority : user.getAuthorities()) {
            System.out.println("authority: " + authority);
        }
        return user;
    }

    /**
     * SecurityExpressionRoot就是SpEL的根对象
     * 用hasPermission还要自己写权限评估器
     * PermissionEvaluator接口默认调用的是DenyAllPermissionEvaluator类 拒绝所有
     * 我们只需要自己写一个Evaluator实现PermissionEvaluator接口并加入容器，系统就会自动调用我们的评估器
     *
     * 如果需要自定义权限用hasPermission或者自己另外定义一个Bean使用SpEL表达式@PreAuthorize("@xx.fun('system:user:test')")
     * 但是最好不要自己定义一个Bean 而是基于Security这个体系去拓展 继承SecurityExpressionRoot这个类去拓展我们自己的权限表达式校验方法
     * 然后将拓展类设置为表达式根对象
     * @return
     */
    @PreAuthorize("hasPermission('system:user:test')")
    //@PreAuthorize("hasPermission('','system:user:test')")
    @GetMapping("/test")
    public String test() {
        return "test success";
    }

    @PreAuthorize("hasAuthority('system:user:add')")
    @GetMapping("/add")
    public String add() {
        return "add success";
    }

    @PreAuthorize("hasAuthority('system:user:select')")
    @GetMapping("/select")
    public String select() {
        return "select success";
    }

    @PreAuthorize("hasAuthority('ROLE_user')")
    @GetMapping("/update")
    public String update() {
        return "update success";
    }

    @PreAuthorize("hasAuthority('system:user:delete')")
    @GetMapping("/delete")
    public String delete() {
        return "delete success";
    }

}
