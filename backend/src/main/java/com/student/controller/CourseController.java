package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.annotation.Log;
import com.student.common.Result;
import com.student.dto.CourseDTO;
import com.student.query.CourseQuery;
import com.student.service.CourseService;
import com.student.vo.CourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "课程管理")
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "分页查询课程")
    @GetMapping
    public Result<IPage<CourseVO>> getPageList(CourseQuery query) {
        return Result.success(courseService.getPageList(query));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "课程详情")
    @GetMapping("/{id}")
    public Result<CourseVO> getById(@PathVariable Long id) {
        return Result.success(courseService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("新增课程")
    @Operation(summary = "新增课程")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody CourseDTO dto) {
        courseService.add(dto);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("修改课程")
    @Operation(summary = "修改课程")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) {
        courseService.update(id, dto);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("删除课程")
    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "所有课程（下拉框用）")
    @GetMapping("/all")
    public Result<List<CourseVO>> getAll() {
        return Result.success(courseService.getAll());
    }
}
