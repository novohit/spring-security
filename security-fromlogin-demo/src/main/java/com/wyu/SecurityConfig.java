package com.wyu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyu.model.User;
import com.wyu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zwx
 * @date 2023-01-11 14:19
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 定义用户第一种方式
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // 定义在内存
//        auth.inMemoryAuthentication()
//                .withUser("novo")
//                .password("1111")
//                .roles("admin")
//                .and()
//                .withUser("zhangsan")
//                .password("1111")
//                .roles("user");
        auth.userDetailsService(userService);
    }


//    /**
//     * 定义用户第二种方式
//     *
//     * @return
//     */
//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("novo")
//                .password("1111")
//                .roles("admin")
//                .build());
//        manager.createUser(User.withUsername("zhangsan")
//                .password("1111")
//                .roles("user")
//                .build());
//        return manager;
//    }

    /**
     * 定义角色继承关系
     *
     * @return
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                // 自定义入参名 默认是username/password
                //.usernameParameter("name")
                //.passwordParameter("pwd")
                .loginPage("/login.html") // 配置登录页地址为login.html 如果没有配置登录接口 则默认接口也是/login.html
                .loginProcessingUrl("/login") // 配置自定义登录接口
                // 配置登录回调  成功/失败 前后端是否分离
                //.successForwardUrl("/toMain") // 请求转发 地址栏仍为/login 无论从什么页面登录都会转发到/toMain的页面
                //.defaultSuccessUrl("/toMain") // 重定向 地址栏变为/toMain 如果是从别的页面登录的登录后会回到那个页面
                //.failureForwardUrl("/toError") // 请求转发
                //.failureUrl("/toError") // 重定向
                // 前后端分离一般用Handler处理
                .successHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    // 这个User就是实现UserDetails那个 没有自定义User则返回的是security中的那个User 如果我们自己自定义了User则返回自定义那个 注意不要导错包
                    User principal = (User) authentication.getPrincipal();
                    System.out.println(request.getRemoteAddr() + " " + principal.getUsername() + " " + principal.getPassword());
                    System.out.println(principal.getAuthorities());//[admin, normal]
                    response.getWriter().write(new ObjectMapper().writeValueAsString(principal));
                })
                .failureHandler((request, response, exception) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(new ObjectMapper().writeValueAsString(exception.getMessage()));
                })
                .permitAll();

        // 授权 从上往下执行 方法顺序有要求 比较笼统的方法放最后 anyRequest只能放最后
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated(); // 其他请求只要认证了就能访问

        // 异常处理
        http.exceptionHandling()
                // 解决认证过的用户访问无权限资源时的异常
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(new ObjectMapper().writeValueAsString("权限不足"));
                })
                // 解决匿名用户访问无权限资源时的异常
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(new ObjectMapper().writeValueAsString("未登录"));
                });

        http.logout()
                .logoutUrl("/logout") // GET请求
                // 前后端不分离
                //.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST")) // 改成POST请求
                //.logoutSuccessUrl("/login.html");
                // 前后端分离 注销成功后的处理
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(new ObjectMapper().writeValueAsString("注销成功"));
                });


        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/css/**", "/js/**", "/images/**");
    }
}
