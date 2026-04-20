package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.BusinessException;
import com.student.dto.ScholarshipApplyDTO;
import com.student.entity.Scholarship;
import com.student.entity.ScholarshipApply;
import com.student.entity.Student;
import com.student.mapper.ScholarshipApplyMapper;
import com.student.mapper.ScholarshipMapper;
import com.student.mapper.StudentMapper;
import com.student.query.ScholarshipApplyQuery;
import com.student.service.ScholarshipApplyService;
import com.student.vo.ScholarshipApplyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScholarshipApplyServiceImpl implements ScholarshipApplyService {

    private final ScholarshipApplyMapper scholarshipApplyMapper;
    private final StudentMapper studentMapper;
    private final ScholarshipMapper scholarshipMapper;

    @Override
    public IPage<ScholarshipApplyVO> getPageList(ScholarshipApplyQuery query) {
        Page<ScholarshipApplyVO> page = new Page<>(query.getPage(), query.getSize());
        return scholarshipApplyMapper.selectPageList(page, query);
    }

    @Override
    public void apply(ScholarshipApplyDTO dto) {
        Student student = studentMapper.selectById(dto.getStudentId());
        if (student == null) throw new BusinessException("学生不存在");

        Scholarship scholarship = scholarshipMapper.selectById(dto.getScholarshipId());
        if (scholarship == null) throw new BusinessException("奖学金不存在");
        if (scholarship.getStatus() == 0) throw new BusinessException("该奖学金已停用");

        ScholarshipApply sa = new ScholarshipApply();
        sa.setStudentId(dto.getStudentId());
        sa.setStudentNo(student.getStudentNo());
        sa.setStudentName(student.getName());
        sa.setScholarshipId(dto.getScholarshipId());
        sa.setScholarshipName(scholarship.getName());
        sa.setApplyReason(dto.getApplyReason());
        sa.setStatus(0);
        sa.setApplyTime(LocalDateTime.now());
        scholarshipApplyMapper.insert(sa);
    }

    @Override
    public void approve(Long id, String approver) {
        ScholarshipApply sa = scholarshipApplyMapper.selectById(id);
        if (sa == null) throw new BusinessException("申请不存在");
        if (sa.getStatus() != 0) throw new BusinessException("该申请已处理");

        sa.setStatus(1);
        sa.setApproveTime(LocalDateTime.now());
        sa.setApprover(approver);
        scholarshipApplyMapper.updateById(sa);
    }

    @Override
    public void reject(Long id, String reason, String approver) {
        ScholarshipApply sa = scholarshipApplyMapper.selectById(id);
        if (sa == null) throw new BusinessException("申请不存在");
        if (sa.getStatus() != 0) throw new BusinessException("该申请已处理");

        sa.setStatus(2);
        sa.setApproveTime(LocalDateTime.now());
        sa.setApprover(approver);
        sa.setRemark(reason);
        scholarshipApplyMapper.updateById(sa);
    }

    @Override
    public IPage<ScholarshipApplyVO> getMyList(Integer page, Integer size, Long studentId) {
        Page<ScholarshipApplyVO> p = new Page<>(page, size);
        return scholarshipApplyMapper.selectMyPageList(p, studentId);
    }
}
