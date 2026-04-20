package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.common.Result;
import com.student.query.SysLogQuery;
import com.student.service.SysLogService;
import com.student.vo.SysLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "操作日志")
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class SysLogController {

    private final SysLogService sysLogService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "分页查询操作日志")
    @GetMapping
    public Result<IPage<SysLogVO>> getPageList(SysLogQuery query) {
        return Result.success(sysLogService.getPageList(query));
    }
}
