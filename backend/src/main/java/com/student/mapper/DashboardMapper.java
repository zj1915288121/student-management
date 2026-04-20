package com.student.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardMapper {

    Long countScoreByRange(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);

    Long countScoreAbove(@Param("minScore") Double minScore);

    List<Map<String, Object>> getClassRanking();
}
