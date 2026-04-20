package com.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.BusinessException;
import com.student.dto.ClassesDTO;
import com.student.entity.Classes;
import com.student.entity.Student;
import com.student.entity.SysUser;
import com.student.mapper.ClassesMapper;
import com.student.mapper.StudentMapper;
import com.student.mapper.SysUserMapper;
import com.student.query.ClassesQuery;
import com.student.service.ClassesService;
import com.student.vo.ClassesVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassesServiceImpl implements ClassesService {

    private final ClassesMapper classesMapper;
    private final StudentMapper studentMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<ClassesVO> getPageList(ClassesQuery query) {
        Page<Classes> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Classes> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(Classes::getClassName, query.getKeyword());
        }
        if (query.getMajor() != null && !query.getMajor().isEmpty()) {
            wrapper.eq(Classes::getMajor, query.getMajor());
        }
        if (query.getGrade() != null && !query.getGrade().isEmpty()) {
            wrapper.eq(Classes::getGrade, query.getGrade());
        }
        wrapper.orderByDesc(Classes::getCreateTime);

        IPage<Classes> classesPage = classesMapper.selectPage(page, wrapper);
        IPage<ClassesVO> voPage = classesPage.convert(this::toVO);
        return voPage;
    }

    @Override
    public ClassesVO getById(Long id) {
        Classes classes = classesMapper.selectById(id);
        if (classes == null) {
            throw new BusinessException("班级不存在");
        }
        return toVO(classes);
    }

    @Override
    public void add(ClassesDTO dto) {
        Classes classes = new Classes();
        BeanUtils.copyProperties(dto, classes);
        classes.setStudentCount(0);
        classesMapper.insert(classes);
    }

    @Override
    public void update(Long id, ClassesDTO dto) {
        Classes classes = classesMapper.selectById(id);
        if (classes == null) {
            throw new BusinessException("班级不存在");
        }
        BeanUtils.copyProperties(dto, classes);
        classes.setId(id);
        classesMapper.updateById(classes);
    }

    @Override
    public void delete(Long id) {
        Long studentCount = studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getClassId, id));
        if (studentCount > 0) {
            throw new BusinessException("该班级下还有学生，无法删除");
        }
        classesMapper.deleteById(id);
    }

    @Override
    public List<Classes> getAll() {
        List<Classes> list = classesMapper.selectList(
                new LambdaQueryWrapper<Classes>().orderByDesc(Classes::getCreateTime));
        for (Classes c : list) {
            if (c.getTeacherId() != null) {
                SysUser teacher = sysUserMapper.selectById(c.getTeacherId());
                if (teacher != null) {
                    c.setTeacherName(teacher.getRealName());
                }
            }
        }
        return list;
    }

    private ClassesVO toVO(Classes classes) {
        ClassesVO vo = new ClassesVO();
        BeanUtils.copyProperties(classes, vo);
        if (classes.getTeacherId() != null && classes.getTeacherName() == null) {
            SysUser teacher = sysUserMapper.selectById(classes.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getRealName());
            }
        }
        return vo;
    }
}
