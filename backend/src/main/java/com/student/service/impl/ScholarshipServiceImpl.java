package com.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.BusinessException;
import com.student.dto.ScholarshipDTO;
import com.student.entity.Scholarship;
import com.student.entity.ScholarshipApply;
import com.student.mapper.ScholarshipApplyMapper;
import com.student.mapper.ScholarshipMapper;
import com.student.query.ScholarshipQuery;
import com.student.service.ScholarshipService;
import com.student.vo.ScholarshipVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScholarshipServiceImpl implements ScholarshipService {

    private final ScholarshipMapper scholarshipMapper;
    private final ScholarshipApplyMapper scholarshipApplyMapper;

    @Override
    public IPage<ScholarshipVO> getPageList(ScholarshipQuery query) {
        Page<Scholarship> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Scholarship> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(Scholarship::getName, query.getKeyword());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Scholarship::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Scholarship::getCreateTime);
        IPage<Scholarship> data = scholarshipMapper.selectPage(page, wrapper);
        return data.convert(this::toVO);
    }

    @Override
    public ScholarshipVO getById(Long id) {
        Scholarship s = scholarshipMapper.selectById(id);
        if (s == null) throw new BusinessException("奖学金不存在");
        return toVO(s);
    }

    @Override
    public void add(ScholarshipDTO dto) {
        Scholarship s = new Scholarship();
        BeanUtils.copyProperties(dto, s);
        if (s.getStatus() == null) s.setStatus(1);
        scholarshipMapper.insert(s);
    }

    @Override
    public void update(Long id, ScholarshipDTO dto) {
        Scholarship s = scholarshipMapper.selectById(id);
        if (s == null) throw new BusinessException("奖学金不存在");
        BeanUtils.copyProperties(dto, s);
        s.setId(id);
        scholarshipMapper.updateById(s);
    }

    @Override
    public void delete(Long id) {
        Long count = scholarshipApplyMapper.selectCount(
                new LambdaQueryWrapper<ScholarshipApply>().eq(ScholarshipApply::getScholarshipId, id));
        if (count > 0) throw new BusinessException("该奖学金已有申请记录，无法删除");
        scholarshipMapper.deleteById(id);
    }

    @Override
    public void toggleStatus(Long id) {
        Scholarship s = scholarshipMapper.selectById(id);
        if (s == null) throw new BusinessException("奖学金不存在");
        s.setStatus(s.getStatus() == 1 ? 0 : 1);
        scholarshipMapper.updateById(s);
    }

    @Override
    public List<ScholarshipVO> getAll() {
        List<Scholarship> list = scholarshipMapper.selectList(
                new LambdaQueryWrapper<Scholarship>().eq(Scholarship::getStatus, 1).orderByDesc(Scholarship::getCreateTime));
        return list.stream().map(this::toVO).toList();
    }

    private ScholarshipVO toVO(Scholarship s) {
        ScholarshipVO vo = new ScholarshipVO();
        BeanUtils.copyProperties(s, vo);
        return vo;
    }
}
