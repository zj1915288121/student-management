package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.StudentDTO;
import com.student.query.StudentQuery;
import com.student.vo.StudentVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface StudentService {

    IPage<StudentVO> getPageList(StudentQuery query);

    StudentVO getById(Long id);

    void add(StudentDTO dto);

    void update(Long id, StudentDTO dto);

    void delete(Long id);

    void batchDelete(String ids);

    Map<String, Object> getStats();

    Map<String, Object> importStudents(MultipartFile file) throws IOException;

    byte[] exportStudents(StudentQuery query) throws IOException;
}
