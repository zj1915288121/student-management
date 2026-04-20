package com.student.controller;

import com.student.annotation.Log;
import com.student.common.Result;
import com.student.config.JwtUserDetails;
import com.student.dto.LoginDTO;
import com.student.service.SysUserService;
import com.student.utils.JwtUtils;
import com.student.utils.LoginRateLimiter;
import com.student.utils.TokenBlacklist;
import com.student.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final JwtUtils jwtUtils;

    @Log("用户登录")
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String rateLimitKey = LoginRateLimiter.buildKey(clientIp, loginDTO.getUsername());

        if (LoginRateLimiter.isLocked(rateLimitKey)) {
            long remaining = LoginRateLimiter.getRemainingLockTime(rateLimitKey);
            return Result.error("登录失败次数过多，请 " + remaining + " 分钟后重试");
        }

        try {
            LoginVO loginVO = sysUserService.login(loginDTO);
            LoginRateLimiter.recordSuccess(rateLimitKey);
            return Result.success(loginVO);
        } catch (Exception e) {
            LoginRateLimiter.recordFailure(rateLimitKey);
            throw e;
        }
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<Map<String, String>> refreshToken(HttpServletRequest request) {
        String oldToken = getTokenFromRequest(request);
        if (!StringUtils.hasText(oldToken)) {
            return Result.error("缺少Token");
        }

        if (TokenBlacklist.isBlacklisted(oldToken)) {
            return Result.error("Token已失效，请重新登录");
        }

        if (!jwtUtils.canRefresh(oldToken)) {
            return Result.error("Token无法刷新，请重新登录");
        }

        Long userId = jwtUtils.getUserIdFromToken(oldToken);
        String username = jwtUtils.getUsernameFromToken(oldToken);
        Integer role = jwtUtils.getRoleFromToken(oldToken);

        long expTime = jwtUtils.getExpirationTime(oldToken);
        TokenBlacklist.add(oldToken, expTime);

        String newToken = jwtUtils.generateToken(userId, username, role);
        return Result.success(Map.of("token", newToken));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<LoginVO.UserInfo> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtUserDetails) {
            JwtUserDetails userDetails = (JwtUserDetails) authentication.getDetails();
            LoginVO.UserInfo userInfo = sysUserService.getUserInfo(userDetails.getUserId());
            return Result.success(userInfo);
        }
        return Result.error("未登录");
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        sysUserService.logout();
        return Result.success();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
