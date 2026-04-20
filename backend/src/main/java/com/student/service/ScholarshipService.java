package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.ScholarshipDTO;
import com.student.query.ScholarshipQuery;
import com.student.vo.ScholarshipVO;

import java.util.List;

public interface ScholarshipService {

    IPage<ScholarshipVO> getPageList(ScholarshipQuery query);

    ScholarshipVO getById(Long id);

    void add(ScholarshipDTO dto);

    void update(Long id, ScholarshipDTO dto);

    void delete(Long id);

    void toggleStatus(Long id);

    List<ScholarshipVO> getAll();
}
