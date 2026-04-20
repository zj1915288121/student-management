package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.ScholarshipApply;
import com.student.query.ScholarshipApplyQuery;
import com.student.vo.ScholarshipApplyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ScholarshipApplyMapper extends BaseMapper<ScholarshipApply> {

    IPage<ScholarshipApplyVO> selectPageList(Page<ScholarshipApplyVO> page, @Param("query") ScholarshipApplyQuery query);

    IPage<ScholarshipApplyVO> selectMyPageList(Page<ScholarshipApplyVO> page, @Param("studentId") Long studentId);
}
