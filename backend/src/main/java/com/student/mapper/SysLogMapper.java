package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.SysLog;
import com.student.query.SysLogQuery;
import com.student.vo.SysLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {

    IPage<SysLogVO> selectPageList(Page<SysLogVO> page, @Param("query") SysLogQuery query);
}
