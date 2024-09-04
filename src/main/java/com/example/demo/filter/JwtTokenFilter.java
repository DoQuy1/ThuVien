package com.example.demo.filter;

import com.example.demo.model.User;
import com.example.demo.service.TokenService;
import com.example.demo.util.JwtUtil;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(value = 2)
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try{
            if(isBypassToken(request)){
                filterChain.doFilter(request,response);
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
            final String token = authHeader.substring(7);
            if(!tokenService.isTokenExists(token)){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is revoked or invalid");
            }
            final String phoneNumber = jwtUtil.extractSubject(token);
            if(phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null){
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if(jwtUtil.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private final List<Pair<String, String>> byPassList = Arrays.asList(
            Pair.of("/api-docs", "GET"),
            Pair.of("/swagger-ui", "GET")
    );
    private boolean isBypassToken(@NonNull HttpServletRequest request){
        String path = request.getServletPath();
        String method = request.getMethod();
        return byPassList
                .stream()
                .anyMatch(pair -> path.contains(pair.getFirst()) && method.equals(pair.getSecond()));
    }
}