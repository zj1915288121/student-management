package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.BusinessException;
import com.student.dto.StatusChangeDTO;
import com.student.entity.Classes;
import com.student.entity.StatusChange;
import com.student.entity.Student;
import com.student.mapper.ClassesMapper;
import com.student.mapper.StatusChangeMapper;
import com.student.mapper.StudentMapper;
import com.student.query.StatusChangeQuery;
import com.student.service.StatusChangeService;
import com.student.vo.StatusChangeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatusChangeServiceImpl implements StatusChangeService {

    private final StatusChangeMapper statusChangeMapper;
    private final StudentMapper studentMapper;
    private final ClassesMapper classesMapper;

    @Override
    public IPage<StatusChangeVO> getPageList(StatusChangeQuery query) {
        Page<StatusChangeVO> page = new Page<>(query.getPage(), query.getSize());
        return statusChangeMapper.selectPageList(page, query);
    }

    @Override
    public StatusChangeVO getById(Long id) {
        StatusChange sc = statusChangeMapper.selectById(id);
        if (sc == null) throw new BusinessException("记录不存在");
        StatusChangeVO vo = new StatusChangeVO();
        BeanUtils.copyProperties(sc, vo);
        return vo;
    }

    @Override
    @Transactional
    public void apply(StatusChangeDTO dto) {
        Student student = studentMapper.selectById(dto.getStudentId());
        if (student == null) throw new BusinessException("学生不存在");

        StatusChange sc = new StatusChange();
        BeanUtils.copyProperties(dto, sc);
        sc.setStudentNo(student.getStudentNo());
        sc.setStudentName(student.getName());
        sc.setStatus(0);
        sc.setApplyTime(LocalDateTime.now());
        statusChangeMapper.insert(sc);
    }

    @Override
    @Transactional
    public void approve(Long id, String approver) {
        StatusChange sc = statusChangeMapper.selectById(id);
        if (sc == null) throw new BusinessException("记录不存在");
        if (sc.getStatus() != 0) throw new BusinessException("该申请已处理");

        sc.setStatus(1);
        sc.setApproveTime(LocalDateTime.now());
        sc.setApprover(approver);
        statusChangeMapper.updateById(sc);

        Student student = studentMapper.selectById(sc.getStudentId());
        if (student == null) return;

        switch (sc.getChangeType()) {
            case 1:
                if (sc.getToInfo() != null) {
                    String[] parts = sc.getToInfo().split("\\|");
                    if (parts.length >= 1) {
                        try {
                            Long newClassId = Long.parseLong(parts[0]);
                            student.setClassId(newClassId);
                            if (parts.length >= 2) student.setMajor(parts[1]);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                break;
            case 2:
                student.setStatus(2);
                break;
            case 3:
                student.setStatus(1);
                break;
            case 4:
                student.setStatus(4);
                break;
        }
        studentMapper.updateById(student);
    }

    @Override
    public void reject(Long id, String reason, String approver) {
        StatusChange sc = statusChangeMapper.selectById(id);
        if (sc == null) throw new BusinessException("记录不存在");
        if (sc.getStatus() != 0) throw new BusinessException("该申请已处理");

        sc.setStatus(2);
        sc.setApproveTime(LocalDateTime.now());
        sc.setApprover(approver);
        if (reason != null) {
            sc.setReason(sc.getReason() + " | 拒绝原因：" + reason);
        }
        statusChangeMapper.updateById(sc);
    }

    @Override
    public void delete(Long id) {
        statusChangeMapper.deleteById(id);
    }

    @Override
    public IPage<StatusChangeVO> getMyList(Integer page, Integer size, Long studentId) {
        Page<StatusChangeVO> p = new Page<>(page, size);
        return statusChangeMapper.selectMyPageList(p, studentId);
    }
}
