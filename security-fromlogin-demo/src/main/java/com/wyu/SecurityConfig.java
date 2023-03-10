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
     * ???????????????????????????
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // ???????????????
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
//     * ???????????????????????????
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
     * ????????????????????????
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
                // ?????????????????? ?????????username/password
                //.usernameParameter("name")
                //.passwordParameter("pwd")
                .loginPage("/login.html") // ????????????????????????login.html ?????????????????????????????? ?????????????????????/login.html
                .loginProcessingUrl("/login") // ???????????????????????????
                // ??????????????????  ??????/?????? ?????????????????????
                //.successForwardUrl("/toMain") // ???????????? ???????????????/login ??????????????????????????????????????????/toMain?????????
                //.defaultSuccessUrl("/toMain") // ????????? ???????????????/toMain ???????????????????????????????????????????????????????????????
                //.failureForwardUrl("/toError") // ????????????
                //.failureUrl("/toError") // ?????????
                // ????????????????????????Handler??????
                .successHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    // ??????User????????????UserDetails?????? ???????????????User???????????????security????????????User ??????????????????????????????User???????????????????????? ?????????????????????
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

        // ?????? ?????????????????? ????????????????????? ?????????????????????????????? anyRequest???????????????
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated(); // ???????????????????????????????????????

        // ????????????
        http.exceptionHandling()
                // ?????????????????????????????????????????????????????????
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(new ObjectMapper().writeValueAsString("????????????"));
                })
                // ???????????????????????????????????????????????????
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(new ObjectMapper().writeValueAsString("?????????"));
                });

        http.logout()
                .logoutUrl("/logout") // GET??????
                // ??????????????????
                //.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST")) // ??????POST??????
                //.logoutSuccessUrl("/login.html");
                // ??????????????? ????????????????????????
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(new ObjectMapper().writeValueAsString("????????????"));
                });


        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/css/**", "/js/**", "/images/**");
    }
}
