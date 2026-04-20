package com.student.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ScoreBatchDTO {

    private Long courseId;
    private String semester;
    private List<ScoreItem> scores;

    @Data
    public static class ScoreItem {
        private Long studentId;
        private BigDecimal score;
        private Integer examType = 1;
    }
}
