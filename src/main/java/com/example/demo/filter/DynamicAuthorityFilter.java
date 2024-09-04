package com.example.demo.filter;

import com.example.demo.model.Role;
import com.example.demo.model.RelaRolePermission;
import com.example.demo.model.User;

import com.example.demo.service.RolePermissionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@Order(value = 3)
@RequiredArgsConstructor
public class DynamicAuthorityFilter extends OncePerRequestFilter {
    private final RolePermissionService rolePermissionService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            filterChain.doFilter(request, response);
            return;
        }
        User user = (User) authentication.getPrincipal();
        if(user.getRole().getName().equals(Role.ADMIN)){
            filterChain.doFilter(request, response);
            return;
        }
        String requestPath = request.getRequestURI();
        List<RelaRolePermission> rolePermissionList = rolePermissionService.findAllByRoleId(user.getRole().getId());
        for(RelaRolePermission rolePermission : rolePermissionList){
            if(requestPath.equals(rolePermission.getPermission().getUrl())){
                filterChain.doFilter(request, response);
                return;
            }
        }
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allow");
    }
}