package com.student.config;

import com.student.common.Result;
import com.student.common.ResultCode;
import com.student.utils.JwtUtils;
import com.student.utils.TokenBlacklist;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            if (TokenBlacklist.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(
                        Result.error(ResultCode.TOKEN_INVALID)));
                return;
            }
            try {
                if (!jwtUtils.isTokenExpired(token)) {
                    String username = jwtUtils.getUsernameFromToken(token);
                    Long userId = jwtUtils.getUserIdFromToken(token);
                    Integer role = jwtUtils.getRoleFromToken(token);

                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (role != null) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + getRoleName(role)));
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authentication.setDetails(new JwtUserDetails(userId, username, role));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(
                        Result.error(ResultCode.TOKEN_INVALID)));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getRoleName(Integer role) {
        return switch (role) {
            case 1 -> "ADMIN";
            case 2 -> "TEACHER";
            case 3 -> "STUDENT";
            default -> "UNKNOWN";
        };
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
