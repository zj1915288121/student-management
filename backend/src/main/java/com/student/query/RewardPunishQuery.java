package com.student.query;

import lombok.Data;

@Data
public class RewardPunishQuery {

    private String keyword;
    private Long studentId;
    private Integer type;
    private String dateStart;
    private String dateEnd;
    private Integer page = 1;
    private Integer size = 10;
}
