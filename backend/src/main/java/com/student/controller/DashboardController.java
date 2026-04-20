package com.student.controller;

import com.student.common.Result;
import com.student.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "首页统计")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "获取首页统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return Result.success(dashboardService.getStats());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "成绩分布")
    @GetMapping("/score-distribution")
    public Result<List<Map<String, Object>>> getScoreDistribution() {
        return Result.success(dashboardService.getScoreDistribution());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "学生状态分布")
    @GetMapping("/student-status")
    public Result<List<Map<String, Object>>> getStudentStatus() {
        return Result.success(dashboardService.getStudentStatus());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "班级平均分排名")
    @GetMapping("/class-ranking")
    public Result<List<Map<String, Object>>> getClassRanking() {
        return Result.success(dashboardService.getClassRanking());
    }
}
