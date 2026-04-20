package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.Student;
import com.student.query.StudentQuery;
import com.student.vo.StudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    IPage<StudentVO> selectPageList(Page<StudentVO> page, @Param("query") StudentQuery query);
}
