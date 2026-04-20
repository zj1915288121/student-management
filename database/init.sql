-- 学生信息管理系统 - 数据库初始化脚本
-- 创建时间：2026-04-18

CREATE DATABASE IF NOT EXISTS student_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE student_management;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role TINYINT NOT NULL DEFAULT 3 COMMENT '角色：1管理员 2教师 3学生',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(50) COMMENT '邮箱',
    avatar VARCHAR(200) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '用户表';

-- 班级表
CREATE TABLE class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    major VARCHAR(50) NOT NULL COMMENT '专业',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    teacher_id BIGINT COMMENT '班主任ID',
    student_count INT DEFAULT 0 COMMENT '学生人数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '班级表';

-- 学生表
CREATE TABLE student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT NOT NULL COMMENT '性别：1男 2女',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    major VARCHAR(50) COMMENT '专业',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(50) COMMENT '邮箱',
    id_card VARCHAR(20) COMMENT '身份证号',
    address VARCHAR(200) COMMENT '家庭住址',
    birth_date DATE COMMENT '出生日期',
    enrollment_date DATE COMMENT '入学日期',
    status TINYINT DEFAULT 1 COMMENT '状态：1在校 2休学 3毕业 4退学',
    avatar VARCHAR(200) COMMENT '头像',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '学生表';

-- 课程表
CREATE TABLE course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(20) COMMENT '课程代码',
    credit DECIMAL(3,1) NOT NULL COMMENT '学分',
    teacher_id BIGINT COMMENT '授课教师ID',
    teacher_name VARCHAR(50) COMMENT '授课教师姓名',
    semester VARCHAR(20) COMMENT '学期',
    class_hours INT COMMENT '学时',
    description TEXT COMMENT '课程描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '课程表';

-- 成绩表
CREATE TABLE score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    student_no VARCHAR(20) COMMENT '学号',
    student_name VARCHAR(50) COMMENT '学生姓名',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    course_name VARCHAR(100) COMMENT '课程名称',
    score DECIMAL(5,2) COMMENT '成绩',
    credit DECIMAL(3,1) COMMENT '学分',
    semester VARCHAR(20) COMMENT '学期',
    exam_type TINYINT DEFAULT 1 COMMENT '考试类型：1正常 2补考 3重修',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '成绩表';

-- 学籍异动表
CREATE TABLE status_change (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    student_no VARCHAR(20) COMMENT '学号',
    student_name VARCHAR(50) COMMENT '学生姓名',
    change_type TINYINT NOT NULL COMMENT '异动类型：1转专业 2休学 3复学 4退学',
    reason VARCHAR(500) COMMENT '异动原因',
    from_info VARCHAR(200) COMMENT '原信息',
    to_info VARCHAR(200) COMMENT '目标信息',
    status TINYINT DEFAULT 0 COMMENT '审批状态：0待审批 1已通过 2已拒绝',
    apply_time DATETIME COMMENT '申请时间',
    approve_time DATETIME COMMENT '审批时间',
    approver VARCHAR(50) COMMENT '审批人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '学籍异动表';

-- 奖惩记录表
CREATE TABLE reward_punish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    student_no VARCHAR(20) COMMENT '学号',
    student_name VARCHAR(50) COMMENT '学生姓名',
    type TINYINT NOT NULL COMMENT '类型：1奖励 2处分',
    level VARCHAR(50) COMMENT '级别',
    content VARCHAR(500) NOT NULL COMMENT '内容',
    reason VARCHAR(500) COMMENT '原因',
    record_date DATE COMMENT '记录日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '奖惩记录表';

-- 奖学金表
CREATE TABLE scholarship (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(100) NOT NULL COMMENT '奖学金名称',
    level VARCHAR(50) COMMENT '等级',
    amount DECIMAL(10,2) COMMENT '金额',
    description VARCHAR(500) COMMENT '说明',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '奖学金表';

-- 奖学金申请表
CREATE TABLE scholarship_apply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    student_no VARCHAR(20) COMMENT '学号',
    student_name VARCHAR(50) COMMENT '学生姓名',
    scholarship_id BIGINT NOT NULL COMMENT '奖学金ID',
    scholarship_name VARCHAR(100) COMMENT '奖学金名称',
    apply_reason VARCHAR(500) COMMENT '申请理由',
    status TINYINT DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝',
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    approve_time DATETIME COMMENT '审核时间',
    approver VARCHAR(50) COMMENT '审核人',
    remark VARCHAR(200) COMMENT '备注'
) COMMENT '奖学金申请表';

-- 操作日志表
CREATE TABLE sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作',
    method VARCHAR(200) COMMENT '方法',
    params TEXT COMMENT '参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    time BIGINT COMMENT '耗时(ms)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '操作日志表';

-- 初始化管理员账号（密码：123456，BCrypt加密）
INSERT INTO sys_user (username, password, role, real_name, status)
VALUES ('admin', '$2b$10$WraGLGpLtwN8Q.HtT95N.uRu3wM9H0NTGPqpsWshDgcL08aNMpKkW', 1, '系统管理员', 1);
