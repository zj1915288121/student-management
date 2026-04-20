package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.RewardPunishDTO;
import com.student.query.RewardPunishQuery;
import com.student.vo.RewardPunishVO;

import java.util.List;

public interface RewardPunishService {

    IPage<RewardPunishVO> getPageList(RewardPunishQuery query);

    void add(RewardPunishDTO dto);

    void update(Long id, RewardPunishDTO dto);

    void delete(Long id);

    List<RewardPunishVO> getByStudentId(Long studentId);

    List<RewardPunishVO> getMyRecords();
}
