package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.CourseDTO;
import com.student.query.CourseQuery;
import com.student.vo.CourseVO;

import java.util.List;

public interface CourseService {

    IPage<CourseVO> getPageList(CourseQuery query);

    CourseVO getById(Long id);

    void add(CourseDTO dto);

    void update(Long id, CourseDTO dto);

    void delete(Long id);

    List<CourseVO> getAll();
}
