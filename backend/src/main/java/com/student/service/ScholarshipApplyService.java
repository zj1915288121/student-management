package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.ScholarshipApplyDTO;
import com.student.query.ScholarshipApplyQuery;
import com.student.vo.ScholarshipApplyVO;

public interface ScholarshipApplyService {

    IPage<ScholarshipApplyVO> getPageList(ScholarshipApplyQuery query);

    void apply(ScholarshipApplyDTO dto);

    void approve(Long id, String approver);

    void reject(Long id, String reason, String approver);

    IPage<ScholarshipApplyVO> getMyList(Integer page, Integer size, Long studentId);
}
