package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.RewardPunish;
import com.student.query.RewardPunishQuery;
import com.student.vo.RewardPunishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RewardPunishMapper extends BaseMapper<RewardPunish> {

    IPage<RewardPunishVO> selectPageList(Page<RewardPunishVO> page, @Param("query") RewardPunishQuery query);

    List<RewardPunishVO> selectByStudentId(@Param("studentId") Long studentId);
}
