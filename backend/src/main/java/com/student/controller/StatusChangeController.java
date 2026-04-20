package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.annotation.Log;
import com.student.common.Result;
import com.student.dto.StatusChangeDTO;
import com.student.query.StatusChangeQuery;
import com.student.service.StatusChangeService;
import com.student.entity.Student;
import com.student.mapper.StudentMapper;
import com.student.utils.SecurityUtils;
import com.student.vo.StatusChangeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "学籍异动管理")
@RestController
@RequestMapping("/status-changes")
@RequiredArgsConstructor
public class StatusChangeController {

    private final StatusChangeService statusChangeService;
    private final StudentMapper studentMapper;

    private Long resolveStudentId() {
        String username = SecurityUtils.getCurrentUsername();
        Student student = studentMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Student>()
                        .eq(Student::getStudentNo, username));
        return student != null ? student.getId() : null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "分页查询异动记录")
    @GetMapping
    public Result<IPage<StatusChangeVO>> getPageList(StatusChangeQuery query) {
        return Result.success(statusChangeService.getPageList(query));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "异动详情")
    @GetMapping("/{id}")
    public Result<StatusChangeVO> getById(@PathVariable Long id) {
        return Result.success(statusChangeService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Log("提交异动申请")
    @Operation(summary = "提交异动申请")
    @PostMapping
    public Result<Void> apply(@Valid @RequestBody StatusChangeDTO dto) {
        statusChangeService.apply(dto);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("审批通过异动")
    @Operation(summary = "审批通过")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        String approver = SecurityUtils.getCurrentUsername();
        statusChangeService.approve(id, approver);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("审批拒绝异动")
    @Operation(summary = "审批拒绝")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestParam(required = false) String reason) {
        String approver = SecurityUtils.getCurrentUsername();
        statusChangeService.reject(id, reason, approver);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("删除异动记录")
    @Operation(summary = "删除异动记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        statusChangeService.delete(id);
        return Result.success();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "我的异动申请")
    @GetMapping("/my")
    public Result<IPage<StatusChangeVO>> getMyList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long studentId = resolveStudentId();
        return Result.success(statusChangeService.getMyList(page, size, studentId));
    }
}
