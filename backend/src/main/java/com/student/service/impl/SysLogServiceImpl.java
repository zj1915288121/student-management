package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.mapper.SysLogMapper;
import com.student.query.SysLogQuery;
import com.student.service.SysLogService;
import com.student.vo.SysLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysLogServiceImpl implements SysLogService {

    private final SysLogMapper sysLogMapper;

    @Override
    public IPage<SysLogVO> getPageList(SysLogQuery query) {
        Page<SysLogVO> page = new Page<>(query.getPage(), query.getSize());
        return sysLogMapper.selectPageList(page, query);
    }
}
