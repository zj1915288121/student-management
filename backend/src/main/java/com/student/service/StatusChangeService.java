package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.StatusChangeDTO;
import com.student.query.StatusChangeQuery;
import com.student.vo.StatusChangeVO;

public interface StatusChangeService {

    IPage<StatusChangeVO> getPageList(StatusChangeQuery query);

    StatusChangeVO getById(Long id);

    void apply(StatusChangeDTO dto);

    void approve(Long id, String approver);

    void reject(Long id, String reason, String approver);

    void delete(Long id);

    IPage<StatusChangeVO> getMyList(Integer page, Integer size, Long studentId);
}
