package com.wyu;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author zwx
 * @date 2023-01-12 15:31
 */
@Configuration
// 开启方法上的权限注解
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    /**
     * 将我们的ExpressionHandler替换掉DefaultMethodSecurityExpressionHandler
     * @return
     */
    @Bean
    CustomMethodSecurityExpressionHandler customMethodSecurityExpressionHandler() {
        return new CustomMethodSecurityExpressionHandler();
    }

    @Bean
    UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("novo")
                .password("{noop}1111")
                // 这里设置了roles和authorities 本质上都是调用的authorities()方法
                // 传进roles后将roles加一个ROLE_前缀转换为authorities集合
                // 所以在Security里代码里是不区分角色和权限的 角色实际上是权限的集合
                // roles()和authorities()只能用一个 会被后一个替代掉
                .roles("admin")
                .authorities("ROLE_user", "system:user:add", "system:user:delete")
                .build());
        manager.createUser(User.withUsername("zhangsan")
                .password("{noop}2222")
                .authorities("system:*")
                .build());
        return manager;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin()
                .permitAll();
        http.authorizeRequests()
                .anyRequest().authenticated();
        http.csrf().disable();
        return http.build();
    }
}
