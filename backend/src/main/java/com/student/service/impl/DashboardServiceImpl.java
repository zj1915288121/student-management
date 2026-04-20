package com.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.entity.Student;
import com.student.entity.SysUser;
import com.student.mapper.ClassesMapper;
import com.student.mapper.CourseMapper;
import com.student.mapper.DashboardMapper;
import com.student.mapper.StudentMapper;
import com.student.mapper.SysUserMapper;
import com.student.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StudentMapper studentMapper;
    private final ClassesMapper classesMapper;
    private final CourseMapper courseMapper;
    private final SysUserMapper sysUserMapper;
    private final DashboardMapper dashboardMapper;

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("studentCount", studentMapper.selectCount(null));
        stats.put("classCount", classesMapper.selectCount(null));
        stats.put("courseCount", courseMapper.selectCount(null));
        stats.put("teacherCount", sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, 2)));
        stats.put("activeStudentCount", studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getStatus, 1)));
        return stats;
    }

    @Override
    public List<Map<String, Object>> getScoreDistribution() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] ranges = {"0-59", "60-69", "70-79", "80-89", "90-100"};
        double[][] conditions = {{0, 60}, {60, 70}, {70, 80}, {80, 90}, {90, -1}};

        for (int i = 0; i < ranges.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("range", ranges[i]);
            try {
                Long count;
                if (conditions[i][1] < 0) {
                    count = dashboardMapper.countScoreAbove(conditions[i][0]);
                } else {
                    count = dashboardMapper.countScoreByRange(conditions[i][0], conditions[i][1]);
                }
                item.put("count", count != null ? count : 0);
            } catch (Exception e) {
                item.put("count", 0);
            }
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getStudentStatus() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] names = {"在校", "休学", "毕业", "退学"};
        Integer[] values = {1, 2, 3, 4};

        for (int i = 0; i < names.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", names[i]);
            item.put("value", studentMapper.selectCount(
                    new LambdaQueryWrapper<Student>().eq(Student::getStatus, values[i])));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getClassRanking() {
        try {
            return dashboardMapper.getClassRanking();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
