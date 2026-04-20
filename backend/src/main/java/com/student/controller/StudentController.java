package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.annotation.Log;
import com.student.common.Result;
import com.student.dto.StudentDTO;
import com.student.query.StudentQuery;
import com.student.service.StudentService;
import com.student.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "学生管理")
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "分页查询学生")
    @GetMapping
    public Result<IPage<StudentVO>> getPageList(StudentQuery query) {
        return Result.success(studentService.getPageList(query));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "学生详情")
    @GetMapping("/{id}")
    public Result<StudentVO> getById(@PathVariable Long id) {
        return Result.success(studentService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("新增学生")
    @Operation(summary = "新增学生")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody StudentDTO dto) {
        studentService.add(dto);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("修改学生")
    @Operation(summary = "修改学生")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody StudentDTO dto) {
        studentService.update(id, dto);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("删除学生")
    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("批量删除学生")
    @Operation(summary = "批量删除学生")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestParam String ids) {
        studentService.batchDelete(ids);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "学生统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return Result.success(studentService.getStats());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "学生搜索（下拉框用）")
    @GetMapping("/search")
    public Result<java.util.List<com.student.vo.StudentVO>> searchStudents(
            @RequestParam(required = false) String keyword) {
        StudentQuery query = new StudentQuery();
        query.setKeyword(keyword);
        query.setPage(1);
        query.setSize(20);
        return Result.success(studentService.getPageList(query).getRecords());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Log("导入学生")
    @Operation(summary = "导入学生Excel")
    @PostMapping("/import")
    public Result<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        return Result.success(studentService.importStudents(file));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "导出学生Excel")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel(StudentQuery query) throws Exception {
        byte[] data = studentService.exportStudents(query);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "下载导入模板")
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() throws Exception {
        StudentQuery emptyQuery = new StudentQuery();
        byte[] data = studentService.exportStudents(emptyQuery);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
