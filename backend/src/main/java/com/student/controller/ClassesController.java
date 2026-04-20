package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.annotation.Log;
import com.student.common.Result;
import com.student.dto.ClassesDTO;
import com.student.entity.Classes;
import com.student.query.ClassesQuery;
import com.student.service.ClassesService;
import com.student.vo.ClassesVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "班级管理")
@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassesController {

    private final ClassesService classesService;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "分页查询班级")
    @GetMapping
    public Result<IPage<ClassesVO>> getPageList(ClassesQuery query) {
        return Result.success(classesService.getPageList(query));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "班级详情")
    @GetMapping("/{id}")
    public Result<ClassesVO> getById(@PathVariable Long id) {
        return Result.success(classesService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("新增班级")
    @Operation(summary = "新增班级")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody ClassesDTO dto) {
        classesService.add(dto);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("修改班级")
    @Operation(summary = "修改班级")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ClassesDTO dto) {
        classesService.update(id, dto);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Log("删除班级")
    @Operation(summary = "删除班级")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        classesService.delete(id);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "所有班级（下拉框用）")
    @GetMapping("/all")
    public Result<List<Classes>> getAll() {
        return Result.success(classesService.getAll());
    }
}
