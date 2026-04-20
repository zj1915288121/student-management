package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.annotation.Log;
import com.student.common.Result;
import com.student.dto.ScholarshipDTO;
import com.student.dto.ScholarshipApplyDTO;
import com.student.query.ScholarshipApplyQuery;
import com.student.query.ScholarshipQuery;
import com.student.service.ScholarshipApplyService;
import com.student.service.ScholarshipService;
import com.student.entity.Student;
import com.student.mapper.StudentMapper;
import com.student.utils.SecurityUtils;
import com.student.vo.ScholarshipApplyVO;
import com.student.vo.ScholarshipVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "奖学金管理")
@RestController
@RequestMapping("/scholarships")
@RequiredArgsConstructor
public class ScholarshipController {

    private final ScholarshipService scholarshipService;
    private final ScholarshipApplyService scholarshipApplyService;
    private final StudentMapper studentMapper;

    private Long resolveStudentId() {
        String username = SecurityUtils.getCurrentUsername();
        Student student = studentMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Student>()
                        .eq(Student::getStudentNo, username));
        return student != null ? student.getId() : null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "分页查询奖学金类型")
    @GetMapping
    public Result<IPage<ScholarshipVO>> getPageList(ScholarshipQuery query) {
        return Result.success(scholarshipService.getPageList(query));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "奖学金详情")
    @GetMapping("/{id}")
    public Result<ScholarshipVO> getById(@PathVariable Long id) {
        return Result.success(scholarshipService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Log("新增奖学金类型")
    @Operation(summary = "新增奖学金类型")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody ScholarshipDTO dto) {
        scholarshipService.add(dto);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Log("修改奖学金类型")
    @Operation(summary = "修改奖学金类型")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ScholarshipDTO dto) {
        scholarshipService.update(id, dto);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("删除奖学金类型")
    @Operation(summary = "删除奖学金类型")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scholarshipService.delete(id);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Log("启用/停用奖学金")
    @Operation(summary = "启用/停用奖学金")
    @PutMapping("/{id}/toggle")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        scholarshipService.toggleStatus(id);
        return Result.success();
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "所有启用的奖学金（下拉框用）")
    @GetMapping("/all")
    public Result<List<ScholarshipVO>> getAll() {
        return Result.success(scholarshipService.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "分页查询奖学金申请")
    @GetMapping("/apply")
    public Result<IPage<ScholarshipApplyVO>> getApplyPageList(ScholarshipApplyQuery query) {
        return Result.success(scholarshipApplyService.getPageList(query));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Log("申请奖学金")
    @Operation(summary = "提交奖学金申请")
    @PostMapping("/apply")
    public Result<Void> apply(@Valid @RequestBody ScholarshipApplyDTO dto) {
        scholarshipApplyService.apply(dto);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("审批通过奖学金")
    @Operation(summary = "审批通过")
    @PutMapping("/apply/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        String approver = SecurityUtils.getCurrentUsername();
        scholarshipApplyService.approve(id, approver);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("审批拒绝奖学金")
    @Operation(summary = "审批拒绝")
    @PutMapping("/apply/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestParam(required = false) String reason) {
        String approver = SecurityUtils.getCurrentUsername();
        scholarshipApplyService.reject(id, reason, approver);
        return Result.success();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "我的奖学金申请")
    @GetMapping("/apply/my")
    public Result<IPage<ScholarshipApplyVO>> getMyApplyList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long studentId = resolveStudentId();
        return Result.success(scholarshipApplyService.getMyList(page, size, studentId));
    }
}
