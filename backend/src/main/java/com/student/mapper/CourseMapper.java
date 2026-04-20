package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.Course;
import com.student.query.CourseQuery;
import com.student.vo.CourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    IPage<CourseVO> selectPageList(Page<CourseVO> page, @Param("query") CourseQuery query);
}
