package com.student.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    Map<String, Object> getStats();

    List<Map<String, Object>> getScoreDistribution();

    List<Map<String, Object>> getStudentStatus();

    List<Map<String, Object>> getClassRanking();
}
