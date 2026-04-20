package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.BusinessException;
import com.student.dto.ScoreBatchDTO;
import com.student.dto.ScoreDTO;
import com.student.entity.Course;
import com.student.entity.Score;
import com.student.entity.Student;
import com.student.mapper.CourseMapper;
import com.student.mapper.ScoreMapper;
import com.student.mapper.StudentMapper;
import com.student.query.ScoreQuery;
import com.student.service.ScoreService;
import com.student.vo.ScoreVO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final ScoreMapper scoreMapper;
    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;

    @Override
    public IPage<ScoreVO> getPageList(ScoreQuery query) {
        Page<ScoreVO> page = new Page<>(query.getPage(), query.getSize());
        IPage<ScoreVO> result = scoreMapper.selectPageList(page, query);
        result.getRecords().forEach(this::fillPassed);
        return result;
    }

    @Override
    public void add(ScoreDTO dto) {
        Score score = new Score();
        BeanUtils.copyProperties(dto, score);
        fillStudentAndCourseInfo(score, dto.getStudentId(), dto.getCourseId());
        if (dto.getExamType() == null) score.setExamType(1);
        scoreMapper.insert(score);
    }

    @Override
    public void batchAdd(ScoreBatchDTO dto) {
        Course course = courseMapper.selectById(dto.getCourseId());
        if (course == null) throw new BusinessException("课程不存在");

        for (ScoreBatchDTO.ScoreItem item : dto.getScores()) {
            Score score = new Score();
            score.setStudentId(item.getStudentId());
            score.setCourseId(dto.getCourseId());
            score.setScore(item.getScore());
            score.setSemester(dto.getSemester());
            score.setExamType(item.getExamType() != null ? item.getExamType() : 1);
            score.setCourseName(course.getCourseName());
            score.setCredit(course.getCredit());
            fillStudentAndCourseInfo(score, item.getStudentId(), dto.getCourseId());
            scoreMapper.insert(score);
        }
    }

    @Override
    public void update(Long id, ScoreDTO dto) {
        Score score = scoreMapper.selectById(id);
        if (score == null) throw new BusinessException("成绩记录不存在");
        BeanUtils.copyProperties(dto, score);
        score.setId(id);
        fillStudentAndCourseInfo(score, dto.getStudentId(), dto.getCourseId());
        scoreMapper.updateById(score);
    }

    @Override
    public void delete(Long id) {
        scoreMapper.deleteById(id);
    }

    @Override
    public List<ScoreVO> getMyScores(Long studentId) {
        List<ScoreVO> list = scoreMapper.selectMyScores(studentId);
        list.forEach(this::fillPassed);
        return list;
    }

    @Override
    public Map<String, Object> getStats(ScoreQuery query) {
        return scoreMapper.selectStats(query);
    }

    @Override
    public List<ScoreVO> getRanking(Long courseId, String semester) {
        List<ScoreVO> list = scoreMapper.selectRanking(courseId, semester);
        list.forEach(this::fillPassed);
        return list;
    }

    @Override
    public List<ScoreVO> getWarning(String semester) {
        List<ScoreVO> list = scoreMapper.selectWarning(semester);
        list.forEach(vo -> vo.setPassed(false));
        return list;
    }

    private void fillStudentAndCourseInfo(Score score, Long studentId, Long courseId) {
        Student student = studentMapper.selectById(studentId);
        if (student != null) {
            score.setStudentNo(student.getStudentNo());
            score.setStudentName(student.getName());
        }
        Course course = courseMapper.selectById(courseId);
        if (course != null) {
            score.setCourseName(course.getCourseName());
            score.setCredit(course.getCredit());
        }
    }

    private void fillPassed(ScoreVO vo) {
        vo.setPassed(vo.getScore() != null && vo.getScore().compareTo(new BigDecimal("60")) >= 0);
    }

    @Override
    public byte[] exportScores(ScoreQuery query) throws IOException {
        query.setPage(1);
        query.setSize(10000);
        IPage<ScoreVO> page = scoreMapper.selectPageList(
                new Page<>(query.getPage(), query.getSize()), query);
        List<ScoreVO> list = page.getRecords();
        list.forEach(this::fillPassed);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("成绩信息");

        String[] headers = {"学号", "姓名", "课程名称", "学期", "成绩", "考试类型", "是否及格"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowIdx = 1;
        for (ScoreVO vo : list) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(vo.getStudentNo() != null ? vo.getStudentNo() : "");
            row.createCell(1).setCellValue(vo.getStudentName() != null ? vo.getStudentName() : "");
            row.createCell(2).setCellValue(vo.getCourseName() != null ? vo.getCourseName() : "");
            row.createCell(3).setCellValue(vo.getSemester() != null ? vo.getSemester() : "");
            row.createCell(4).setCellValue(vo.getScore() != null ? vo.getScore().doubleValue() : 0);
            String examType = vo.getExamType() != null ?
                    (vo.getExamType() == 1 ? "正常" : vo.getExamType() == 2 ? "补考" : "重修") : "";
            row.createCell(5).setCellValue(examType);
            row.createCell(6).setCellValue(vo.getPassed() != null && vo.getPassed() ? "及格" : "不及格");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}
