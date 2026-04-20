package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.Score;
import com.student.query.ScoreQuery;
import com.student.vo.ScoreVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    IPage<ScoreVO> selectPageList(Page<ScoreVO> page, @Param("query") ScoreQuery query);

    List<ScoreVO> selectMyScores(@Param("studentId") Long studentId);

    Map<String, Object> selectStats(@Param("query") ScoreQuery query);

    List<ScoreVO> selectRanking(@Param("courseId") Long courseId, @Param("semester") String semester);

    List<ScoreVO> selectWarning(@Param("semester") String semester);
}
