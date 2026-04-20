package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.query.SysLogQuery;
import com.student.vo.SysLogVO;

public interface SysLogService {

    IPage<SysLogVO> getPageList(SysLogQuery query);
}
