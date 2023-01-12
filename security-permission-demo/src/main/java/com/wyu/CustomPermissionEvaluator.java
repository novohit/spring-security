package com.wyu;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.Serializable;
import java.util.Collection;

/**
 * hasPermission('','system:user:test')"的自定义权限评估器
 * 注入后并加入容器，系统就会自动调用我们的评估器替代DenyAllPermissionEvaluator
 * @author zwx
 * @date 2023-01-12 16:58
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * targetDomainObject参数暂时用不到
     * @param authentication
     * @param targetDomainObject
     * @param permission
     * @return
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // 获取当前用户所有权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            // 如果要使用通配符* 可以用antPathMatcher
            // system:*  system.user.add
            if (antPathMatcher.match(authority.getAuthority(), (String) permission)) {
                return true;
            }
//            if (authority.getAuthority().equals(permission)) {
//                return true;
//            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
