package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.BusinessException;
import com.student.dto.RewardPunishDTO;
import com.student.entity.RewardPunish;
import com.student.entity.Student;
import com.student.mapper.RewardPunishMapper;
import com.student.mapper.StudentMapper;
import com.student.query.RewardPunishQuery;
import com.student.service.RewardPunishService;
import com.student.vo.RewardPunishVO;
import com.student.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardPunishServiceImpl implements RewardPunishService {

    private final RewardPunishMapper rewardPunishMapper;
    private final StudentMapper studentMapper;

    @Override
    public IPage<RewardPunishVO> getPageList(RewardPunishQuery query) {
        Page<RewardPunishVO> page = new Page<>(query.getPage(), query.getSize());
        return rewardPunishMapper.selectPageList(page, query);
    }

    @Override
    public void add(RewardPunishDTO dto) {
        Student student = studentMapper.selectById(dto.getStudentId());
        if (student == null) throw new BusinessException("学生不存在");

        RewardPunish rp = new RewardPunish();
        BeanUtils.copyProperties(dto, rp);
        rp.setStudentNo(student.getStudentNo());
        rp.setStudentName(student.getName());
        rewardPunishMapper.insert(rp);
    }

    @Override
    public void update(Long id, RewardPunishDTO dto) {
        RewardPunish rp = rewardPunishMapper.selectById(id);
        if (rp == null) throw new BusinessException("记录不存在");

        Student student = studentMapper.selectById(dto.getStudentId());
        if (student == null) throw new BusinessException("学生不存在");

        BeanUtils.copyProperties(dto, rp);
        rp.setId(id);
        rp.setStudentNo(student.getStudentNo());
        rp.setStudentName(student.getName());
        rewardPunishMapper.updateById(rp);
    }

    @Override
    public void delete(Long id) {
        rewardPunishMapper.deleteById(id);
    }

    @Override
    public List<RewardPunishVO> getByStudentId(Long studentId) {
        return rewardPunishMapper.selectByStudentId(studentId);
    }

    @Override
    public List<RewardPunishVO> getMyRecords() {
        String username = SecurityUtils.getCurrentUsername();
        Student student = studentMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Student>()
                        .eq(Student::getStudentNo, username));
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        return rewardPunishMapper.selectByStudentId(student.getId());
    }
}
