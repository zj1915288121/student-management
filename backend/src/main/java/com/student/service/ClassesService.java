package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.ClassesDTO;
import com.student.entity.Classes;
import com.student.query.ClassesQuery;
import com.student.vo.ClassesVO;

import java.util.List;

public interface ClassesService {

    IPage<ClassesVO> getPageList(ClassesQuery query);

    ClassesVO getById(Long id);

    void add(ClassesDTO dto);

    void update(Long id, ClassesDTO dto);

    void delete(Long id);

    List<Classes> getAll();
}
