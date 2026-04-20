package com.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.BusinessException;
import com.student.dto.CourseDTO;
import com.student.entity.Course;
import com.student.entity.Score;
import com.student.mapper.CourseMapper;
import com.student.mapper.ScoreMapper;
import com.student.query.CourseQuery;
import com.student.service.CourseService;
import com.student.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final ScoreMapper scoreMapper;

    @Override
    public IPage<CourseVO> getPageList(CourseQuery query) {
        Page<CourseVO> page = new Page<>(query.getPage(), query.getSize());
        return courseMapper.selectPageList(page, query);
    }

    @Override
    public CourseVO getById(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) throw new BusinessException("课程不存在");
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(course, vo);
        return vo;
    }

    @Override
    public void add(CourseDTO dto) {
        Course course = new Course();
        BeanUtils.copyProperties(dto, course);
        courseMapper.insert(course);
    }

    @Override
    public void update(Long id, CourseDTO dto) {
        Course course = courseMapper.selectById(id);
        if (course == null) throw new BusinessException("课程不存在");
        BeanUtils.copyProperties(dto, course);
        course.setId(id);
        courseMapper.updateById(course);
    }

    @Override
    public void delete(Long id) {
        Long scoreCount = scoreMapper.selectCount(
                new LambdaQueryWrapper<Score>().eq(Score::getCourseId, id));
        if (scoreCount > 0) {
            throw new BusinessException("该课程已有成绩记录，无法删除");
        }
        courseMapper.deleteById(id);
    }

    @Override
    public List<CourseVO> getAll() {
        Page<CourseVO> page = new Page<>(1, 1000);
        CourseQuery query = new CourseQuery();
        return courseMapper.selectPageList(page, query).getRecords();
    }
}
