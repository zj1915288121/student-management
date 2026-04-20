package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.annotation.Log;
import com.student.common.Result;
import com.student.dto.ChangePasswordDTO;
import com.student.dto.ProfileUpdateDTO;
import com.student.dto.SysUserDTO;
import com.student.query.SysUserQuery;
import com.student.service.SysUserService;
import com.student.utils.SecurityUtils;
import com.student.vo.SysUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "分页查询用户")
    @GetMapping
    public Result<IPage<SysUserVO>> getPageList(SysUserQuery query) {
        return Result.success(sysUserService.getPageList(query));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public Result<SysUserVO> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("新增用户")
    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SysUserDTO dto) {
        sysUserService.add(dto);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("修改用户")
    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysUserDTO dto) {
        sysUserService.update(id, dto);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("删除用户")
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("重置密码")
    @Operation(summary = "重置密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        sysUserService.resetPassword(id);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("启用/禁用用户")
    @Operation(summary = "启用/禁用用户")
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        sysUserService.toggleStatus(id);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "角色列表")
    @GetMapping("/roles")
    public Result<java.util.List<java.util.Map<String, Object>>> getRoles() {
        java.util.List<java.util.Map<String, Object>> roles = new java.util.ArrayList<>();
        roles.add(java.util.Map.of("value", 1, "label", "管理员"));
        roles.add(java.util.Map.of("value", 2, "label", "教师"));
        roles.add(java.util.Map.of("value", 3, "label", "学生"));
        return Result.success(roles);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "角色人数统计")
    @GetMapping("/role-counts")
    public Result<java.util.Map<Integer, Long>> getRoleCounts() {
        return Result.success(sysUserService.getRoleCounts());
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<SysUserVO> getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(sysUserService.getProfile(userId));
    }

    @PreAuthorize("isAuthenticated()")
    @Log("修改个人信息")
    @Operation(summary = "修改个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody ProfileUpdateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        sysUserService.updateProfile(userId, dto);
        return Result.success();
    }

    @PreAuthorize("isAuthenticated()")
    @Log("修改密码")
    @Operation(summary = "修改密码")
    @PutMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        sysUserService.changePassword(dto, userId);
        return Result.success();
    }
}
