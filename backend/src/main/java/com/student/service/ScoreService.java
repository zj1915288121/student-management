package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.ScoreBatchDTO;
import com.student.dto.ScoreDTO;
import com.student.query.ScoreQuery;
import com.student.vo.ScoreVO;

import java.util.List;
import java.util.Map;

public interface ScoreService {

    IPage<ScoreVO> getPageList(ScoreQuery query);

    void add(ScoreDTO dto);

    void batchAdd(ScoreBatchDTO dto);

    void update(Long id, ScoreDTO dto);

    void delete(Long id);

    List<ScoreVO> getMyScores(Long studentId);

    Map<String, Object> getStats(ScoreQuery query);

    List<ScoreVO> getRanking(Long courseId, String semester);

    List<ScoreVO> getWarning(String semester);

    byte[] exportScores(ScoreQuery query) throws java.io.IOException;
}
