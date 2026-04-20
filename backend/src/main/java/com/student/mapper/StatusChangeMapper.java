package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.StatusChange;
import com.student.query.StatusChangeQuery;
import com.student.vo.StatusChangeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StatusChangeMapper extends BaseMapper<StatusChange> {

    IPage<StatusChangeVO> selectPageList(Page<StatusChangeVO> page, @Param("query") StatusChangeQuery query);

    IPage<StatusChangeVO> selectMyPageList(Page<StatusChangeVO> page, @Param("studentId") Long studentId);
}
