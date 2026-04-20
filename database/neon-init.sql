-- ============================================
-- 学生信息管理系统 - Neon PostgreSQL 完整初始化
-- 执行时间：2026-04-20
-- 密码统一：123456
-- BCrypt哈希：$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW
-- 注意：必须使用 $2a$ 前缀（Spring Security 兼容），不能用 $2b$
-- ============================================

-- 清空所有表数据（按外键依赖顺序）
TRUNCATE scholarship_apply RESTART IDENTITY CASCADE;
TRUNCATE reward_punish RESTART IDENTITY CASCADE;
TRUNCATE enrollment_change RESTART IDENTITY CASCADE;
TRUNCATE score RESTART IDENTITY CASCADE;
TRUNCATE operation_log RESTART IDENTITY CASCADE;
TRUNCATE student RESTART IDENTITY CASCADE;
TRUNCATE course RESTART IDENTITY CASCADE;
TRUNCATE "class" RESTART IDENTITY CASCADE;
TRUNCATE teacher RESTART IDENTITY CASCADE;
TRUNCATE sys_user RESTART IDENTITY CASCADE;
TRUNCATE scholarship RESTART IDENTITY CASCADE;

-- ============================================
-- 1. 系统用户表（admin + 教师 + 学生）
-- ============================================

-- 管理员账号
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '系统管理员', 1, 1);

-- 教师用户（T001-T010）
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('T001', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '王建国', 2, 1),
('T002', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '刘芳', 2, 1),
('T003', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '陈志远', 2, 1),
('T004', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '赵敏', 2, 1),
('T005', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '周明', 2, 1);

-- 学生用户（20240001-20240010）
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('20240001', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '张伟', 3, 1),
('20240002', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '李娜', 3, 1),
('20240003', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '王强', 3, 1),
('20240004', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '赵雪', 3, 1),
('20240005', '$2a$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', '刘洋', 3, 1);

-- ============================================
-- 2. 教师信息表
-- ============================================
INSERT INTO teacher (teacher_no, name, gender, phone, email, title, department) VALUES
('T001', '王建国', '男', '13901001001', 'wangjg@school.edu.cn', '教授', '计算机学院'),
('T002', '刘芳', '女', '13901001002', 'liuf@school.edu.cn', '副教授', '计算机学院'),
('T003', '陈志远', '男', '13901001003', 'chenzy@school.edu.cn', '讲师', '软件学院'),
('T004', '赵敏', '女', '13901001004', 'zhaom@school.edu.cn', '教授', '数据科学学院'),
('T005', '周明', '男', '13901001005', 'zhoum@school.edu.cn', '副教授', '人工智能学院');

-- 关联 sys_user.teacher_id
UPDATE sys_user SET teacher_id = 1 WHERE username = 'T001';
UPDATE sys_user SET teacher_id = 2 WHERE username = 'T002';
UPDATE sys_user SET teacher_id = 3 WHERE username = 'T003';
UPDATE sys_user SET teacher_id = 4 WHERE username = 'T004';
UPDATE sys_user SET teacher_id = 5 WHERE username = 'T005';

-- ============================================
-- 3. 班级表
-- ============================================
INSERT INTO "class" (class_no, class_name, teacher_id, student_count) VALUES
('CS202401', '计算机科学2401班', 1, 5),
('CS202402', '计算机科学2402班', 2, 5),
('SE202401', '软件工程2401班', 3, 0),
('AI202401', '人工智能2401班', 4, 0),
('IS202401', '信息安全2401班', 5, 0);

-- ============================================
-- 4. 学生信息表
-- ============================================
INSERT INTO student (student_no, name, gender, phone, email, class_id, major, enrollment_year, status) VALUES
-- 计算机2401班（5人）
('20240001', '张伟', '男', '13800001001', 'zhangwei@stu.edu.cn', 1, '计算机科学与技术', 2024, '在读'),
('20240002', '李娜', '女', '13800001002', 'lina@stu.edu.cn', 1, '计算机科学与技术', 2024, '在读'),
('20240003', '王强', '男', '13800001003', 'wangqiang@stu.edu.cn', 1, '计算机科学与技术', 2024, '在读'),
('20240004', '赵雪', '女', '13800001004', 'zhaoxue@stu.edu.cn', 1, '计算机科学与技术', 2024, '在读'),
('20240005', '刘洋', '男', '13800001005', 'liuyang@stu.edu.cn', 1, '计算机科学与技术', 2024, '在读');

-- 关联 sys_user.student_id
UPDATE sys_user SET student_id = 1 WHERE username = '20240001';
UPDATE sys_user SET student_id = 2 WHERE username = '20240002';
UPDATE sys_user SET student_id = 3 WHERE username = '20240003';
UPDATE sys_user SET student_id = 4 WHERE username = '20240004';
UPDATE sys_user SET student_id = 5 WHERE username = '20240005';

-- ============================================
-- 5. 课程表
-- ============================================
INSERT INTO course (course_no, course_name, credit, hours, teacher_id, semester, course_type) VALUES
('CS101', '高等数学', 4.0, 64, 1, '2024-1', '必修'),
('CS102', '线性代数', 3.0, 48, 1, '2024-1', '必修'),
('CS103', '概率论与数理统计', 3.0, 48, 2, '2024-2', '必修'),
('CS104', 'C语言程序设计', 3.0, 48, 2, '2024-1', '必修'),
('CS105', '数据结构与算法', 4.0, 64, 3, '2024-2', '必修'),
('CS106', '数据库原理', 3.0, 48, 3, '2024-2', '必修'),
('CS107', '操作系统', 3.0, 48, 4, '2025-1', '必修'),
('CS108', '计算机网络', 3.0, 48, 4, '2025-1', '必修'),
('CS109', '人工智能导论', 2.0, 32, 5, '2025-1', '选修'),
('CS110', '软件工程', 3.0, 48, 5, '2025-1', '必修');

-- ============================================
-- 6. 成绩表（代表性样本）
-- ============================================
INSERT INTO score (student_id, course_id, score, exam_date) VALUES
-- 20240001 张伟的成绩
(1, 1, 92.0, '2024-12-20'),
(1, 2, 88.0, '2024-12-20'),
(1, 4, 95.0, '2024-12-20'),
-- 20240002 李娜的成绩
(2, 1, 85.0, '2024-12-20'),
(2, 2, 91.0, '2024-12-20'),
(2, 4, 87.0, '2024-12-20'),
-- 20240003 王强的成绩（有不及格）
(3, 1, 55.0, '2024-12-20'),
(3, 2, 48.0, '2024-12-20'),
(3, 4, 62.0, '2024-12-20'),
-- 20240004 赵雪的成绩
(4, 1, 78.0, '2024-12-20'),
(4, 2, 82.0, '2024-12-20'),
(4, 4, 76.0, '2024-12-20'),
-- 20240005 刘洋的成绩（优秀）
(5, 1, 98.0, '2024-12-20'),
(5, 2, 96.0, '2024-12-20'),
(5, 4, 99.0, '2024-12-20');

-- ============================================
-- 7. 学籍异动表
-- ============================================
INSERT INTO enrollment_change (student_id, change_type, change_date, reason, status) VALUES
(3, '休学', '2025-01-15', '身体原因需休学一年', 'approved');

-- ============================================
-- 8. 奖惩记录表
-- ============================================
INSERT INTO reward_punish (student_id, type, level, title, description, date) VALUES
(1, '奖励', '校级', '三好学生', '学习成绩优异，思想品德良好', '2025-01-10'),
(5, '奖励', '省级', '优秀学生干部', '担任班级干部工作出色', '2025-01-10'),
(3, '处分', '警告', '学业预警', '多门课程不及格', '2025-01-15');

-- ============================================
-- 9. 奖学金类型表
-- ============================================
INSERT INTO scholarship (name, type, amount, description, criteria) VALUES
('国家奖学金', '国家级', 8000.00, '奖励特别优秀的学生', '学年成绩排名前5%，无不及格科目'),
('国家励志奖学金', '国家级', 5000.00, '奖励家庭经济困难但品学兼优的学生', '家庭经济困难，学年成绩排名前30%'),
('学校一等奖学金', '校级', 3000.00, '奖励学年成绩优秀学生', '学年成绩排名前10%'),
('学校二等奖学金', '校级', 2000.00, '奖励学年成绩优良学生', '学年成绩排名前20%'),
('学校三等奖学金', '校级', 1000.00, '奖励学年成绩良好学生', '学年成绩排名前30%');

-- ============================================
-- 10. 奖学金申请表
-- ============================================
INSERT INTO scholarship_apply (scholarship_id, student_id, apply_reason, status, apply_date) VALUES
(1, 5, '学年成绩专业第一，综合素质优秀', 'approved', '2025-03-01'),
(3, 1, '学年成绩班级第二', 'approved', '2025-03-02'),
(2, 2, '家庭经济困难，成绩优良', 'pending', '2025-03-05'),
(4, 4, '成绩稳定，表现良好', 'pending', '2025-03-06');

-- ============================================
-- 完成！验证查询
-- ============================================
-- 执行完成后，运行以下查询验证数据：
-- SELECT username, real_name, role FROM sys_user ORDER BY role, username;
-- SELECT student_no, name, major FROM student;
-- SELECT class_no, class_name FROM "class";
-- SELECT course_no, course_name, credit FROM course;
-- SELECT COUNT(*) as score_count FROM score;
