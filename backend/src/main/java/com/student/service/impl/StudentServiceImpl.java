package com.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.BusinessException;
import com.student.dto.StudentDTO;
import com.student.entity.Classes;
import com.student.entity.Student;
import com.student.entity.SysUser;
import com.student.mapper.ClassesMapper;
import com.student.mapper.StudentMapper;
import com.student.mapper.SysUserMapper;
import com.student.query.StudentQuery;
import com.student.service.StudentService;
import com.student.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final SysUserMapper sysUserMapper;
    private final ClassesMapper classesMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public IPage<StudentVO> getPageList(StudentQuery query) {
        Page<StudentVO> page = new Page<>(query.getPage(), query.getSize());
        return studentMapper.selectPageList(page, query);
    }

    @Override
    public StudentVO getById(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        StudentVO vo = new StudentVO();
        BeanUtils.copyProperties(student, vo);
        if (student.getClassId() != null) {
            Classes classes = classesMapper.selectById(student.getClassId());
            if (classes != null) {
                vo.setClassName(classes.getClassName());
            }
        }
        return vo;
    }

    @Override
    @Transactional
    public void add(StudentDTO dto) {
        Long count = studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, dto.getStudentNo()));
        if (count > 0) {
            throw new BusinessException("学号已存在");
        }

        Student student = new Student();
        BeanUtils.copyProperties(dto, student);
        if (student.getStatus() == null) {
            student.setStatus(1);
        }
        studentMapper.insert(student);

        SysUser user = new SysUser();
        user.setUsername(dto.getStudentNo());
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole(3);
        user.setRealName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(1);
        sysUserMapper.insert(user);

        if (dto.getClassId() != null) {
            Classes classes = classesMapper.selectById(dto.getClassId());
            if (classes != null) {
                classes.setStudentCount(classes.getStudentCount() + 1);
                classesMapper.updateById(classes);
            }
        }
    }

    @Override
    @Transactional
    public void update(Long id, StudentDTO dto) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        Long classIdOld = student.getClassId();
        Long classIdNew = dto.getClassId();

        BeanUtils.copyProperties(dto, student);
        student.setId(id);
        studentMapper.updateById(student);

        if (classIdOld != null && !classIdOld.equals(classIdNew)) {
            Classes oldClass = classesMapper.selectById(classIdOld);
            if (oldClass != null && oldClass.getStudentCount() > 0) {
                oldClass.setStudentCount(oldClass.getStudentCount() - 1);
                classesMapper.updateById(oldClass);
            }
        }
        if (classIdNew != null && !classIdNew.equals(classIdOld)) {
            Classes newClass = classesMapper.selectById(classIdNew);
            if (newClass != null) {
                newClass.setStudentCount(newClass.getStudentCount() + 1);
                classesMapper.updateById(newClass);
            }
        }

        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, student.getStudentNo()));
        if (user != null) {
            user.setRealName(dto.getName());
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            sysUserMapper.updateById(user);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        studentMapper.deleteById(id);

        sysUserMapper.delete(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, student.getStudentNo()));

        if (student.getClassId() != null) {
            Classes classes = classesMapper.selectById(student.getClassId());
            if (classes != null && classes.getStudentCount() > 0) {
                classes.setStudentCount(classes.getStudentCount() - 1);
                classesMapper.updateById(classes);
            }
        }
    }

    @Override
    @Transactional
    public void batchDelete(String ids) {
        String[] idArr = ids.split(",");
        for (String idStr : idArr) {
            delete(Long.parseLong(idStr.trim()));
        }
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", studentMapper.selectCount(null));
        stats.put("active", studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getStatus, 1)));
        stats.put("suspended", studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getStatus, 2)));
        stats.put("graduated", studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getStatus, 3)));
        return stats;
    }

    @Override
    @Transactional
    public Map<String, Object> importStudents(MultipartFile file) throws IOException {
        int successCount = 0;
        int failCount = 0;
        List<String> failReasons = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                String studentNo = getCellStringValue(row.getCell(0));
                String name = getCellStringValue(row.getCell(1));
                String genderStr = getCellStringValue(row.getCell(2));
                String className = getCellStringValue(row.getCell(3));
                String major = getCellStringValue(row.getCell(4));
                String phone = getCellStringValue(row.getCell(5));
                String email = getCellStringValue(row.getCell(6));

                if (studentNo.isEmpty() || name.isEmpty()) {
                    failCount++;
                    failReasons.add("第" + (i + 1) + "行：学号和姓名不能为空");
                    continue;
                }

                Long count = studentMapper.selectCount(
                        new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, studentNo));
                if (count > 0) {
                    failCount++;
                    failReasons.add("第" + (i + 1) + "行：学号" + studentNo + "已存在");
                    continue;
                }

                Student student = new Student();
                student.setStudentNo(studentNo);
                student.setName(name);
                student.setGender("女".equals(genderStr) ? 2 : 1);
                student.setMajor(major);
                student.setPhone(phone);
                student.setEmail(email);
                student.setStatus(1);
                student.setEnrollmentDate(LocalDate.now());

                if (!className.isEmpty()) {
                    Classes cls = classesMapper.selectOne(
                            new LambdaQueryWrapper<Classes>().eq(Classes::getClassName, className));
                    if (cls != null) {
                        student.setClassId(cls.getId());
                        student.setMajor(cls.getMajor());
                    }
                }

                studentMapper.insert(student);

                SysUser user = new SysUser();
                user.setUsername(studentNo);
                user.setPassword(passwordEncoder.encode("123456"));
                user.setRole(3);
                user.setRealName(name);
                user.setPhone(phone);
                user.setEmail(email);
                user.setStatus(1);
                sysUserMapper.insert(user);

                if (student.getClassId() != null) {
                    Classes cls = classesMapper.selectById(student.getClassId());
                    if (cls != null) {
                        cls.setStudentCount(cls.getStudentCount() + 1);
                        classesMapper.updateById(cls);
                    }
                }

                successCount++;
            } catch (Exception e) {
                failCount++;
                failReasons.add("第" + (i + 1) + "行：" + e.getMessage());
            }
        }
        workbook.close();

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failReasons", failReasons);
        return result;
    }

    @Override
    public byte[] exportStudents(StudentQuery query) throws IOException {
        query.setPage(1);
        query.setSize(100000);
        IPage<StudentVO> page = studentMapper.selectPageList(
                new Page<>(query.getPage(), query.getSize()), query);
        List<StudentVO> list = page.getRecords();

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("学生信息");

        String[] headers = {"学号", "姓名", "性别", "班级", "专业", "手机", "邮箱", "状态", "入学日期"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowIdx = 1;
        for (StudentVO vo : list) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(vo.getStudentNo());
            row.createCell(1).setCellValue(vo.getName());
            row.createCell(2).setCellValue(vo.getGender() == 1 ? "男" : "女");
            row.createCell(3).setCellValue(vo.getClassName() != null ? vo.getClassName() : "");
            row.createCell(4).setCellValue(vo.getMajor() != null ? vo.getMajor() : "");
            row.createCell(5).setCellValue(vo.getPhone() != null ? vo.getPhone() : "");
            row.createCell(6).setCellValue(vo.getEmail() != null ? vo.getEmail() : "");
            row.createCell(7).setCellValue(getStatusText(vo.getStatus()));
            row.createCell(8).setCellValue(vo.getEnrollmentDate() != null ? vo.getEnrollmentDate().toString() : "");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.dispose();
        workbook.close();
        return out.toByteArray();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 1 -> "在校";
            case 2 -> "休学";
            case 3 -> "毕业";
            case 4 -> "退学";
            default -> "未知";
        };
    }
}
