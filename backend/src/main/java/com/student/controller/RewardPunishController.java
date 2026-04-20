package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.annotation.Log;
import com.student.common.Result;
import com.student.dto.RewardPunishDTO;
import com.student.query.RewardPunishQuery;
import com.student.service.RewardPunishService;
import com.student.utils.SecurityUtils;
import com.student.vo.RewardPunishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "奖惩管理")
@RestController
@RequestMapping("/reward-punish")
@RequiredArgsConstructor
public class RewardPunishController {

    private final RewardPunishService rewardPunishService;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "分页查询奖惩记录")
    @GetMapping
    public Result<IPage<RewardPunishVO>> getPageList(RewardPunishQuery query) {
        return Result.success(rewardPunishService.getPageList(query));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("新增奖惩记录")
    @Operation(summary = "新增奖惩记录")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody RewardPunishDTO dto) {
        rewardPunishService.add(dto);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("修改奖惩记录")
    @Operation(summary = "修改奖惩记录")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RewardPunishDTO dto) {
        rewardPunishService.update(id, dto);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("删除奖惩记录")
    @Operation(summary = "删除奖惩记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        rewardPunishService.delete(id);
        return Result.success();
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "按学生查询奖惩记录")
    @GetMapping("/student/{studentId}")
    public Result<List<RewardPunishVO>> getByStudentId(@PathVariable Long studentId) {
        if (SecurityUtils.isStudent()) {
            SecurityUtils.checkSelfOrAdmin(studentId);
        }
        return Result.success(rewardPunishService.getByStudentId(studentId));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "我的奖惩记录")
    @GetMapping("/my")
    public Result<List<RewardPunishVO>> getMyRecords() {
        return Result.success(rewardPunishService.getMyRecords());
    }
}
