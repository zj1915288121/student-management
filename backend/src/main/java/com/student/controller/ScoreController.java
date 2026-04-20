package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.annotation.Log;
import com.student.common.Result;
import com.student.dto.ScoreBatchDTO;
import com.student.dto.ScoreDTO;
import com.student.query.ScoreQuery;
import com.student.service.ScoreService;
import com.student.entity.Student;
import com.student.mapper.StudentMapper;
import com.student.utils.SecurityUtils;
import com.student.vo.ScoreVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "成绩管理")
@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;
    private final StudentMapper studentMapper;

    private Long resolveStudentId() {
        String username = SecurityUtils.getCurrentUsername();
        Student student = studentMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Student>()
                        .eq(Student::getStudentNo, username));
        return student != null ? student.getId() : null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "分页查询成绩")
    @GetMapping
    public Result<IPage<ScoreVO>> getPageList(ScoreQuery query) {
        return Result.success(scoreService.getPageList(query));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("录入成绩")
    @Operation(summary = "录入单条成绩")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody ScoreDTO dto) {
        scoreService.add(dto);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("批量录入成绩")
    @Operation(summary = "批量录入成绩")
    @PostMapping("/batch")
    public Result<Void> batchAdd(@RequestBody ScoreBatchDTO dto) {
        scoreService.batchAdd(dto);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("修改成绩")
    @Operation(summary = "修改成绩")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ScoreDTO dto) {
        scoreService.update(id, dto);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("删除成绩")
    @Operation(summary = "删除成绩")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scoreService.delete(id);
        return Result.success();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "我的成绩")
    @GetMapping("/my")
    public Result<List<ScoreVO>> getMyScores() {
        Long studentId = resolveStudentId();
        return Result.success(scoreService.getMyScores(studentId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "成绩统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(ScoreQuery query) {
        return Result.success(scoreService.getStats(query));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "成绩排名")
    @GetMapping("/ranking")
    public Result<List<ScoreVO>> getRanking(
            @RequestParam Long courseId,
            @RequestParam(required = false) String semester) {
        return Result.success(scoreService.getRanking(courseId, semester));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "挂科预警")
    @GetMapping("/warning")
    public Result<List<ScoreVO>> getWarning(
            @RequestParam(required = false) String semester) {
        return Result.success(scoreService.getWarning(semester));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "导出成绩Excel")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel(ScoreQuery query) throws Exception {
        byte[] data = scoreService.exportScores(query);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scores.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
