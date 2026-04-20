-- 学生信息管理系统 PostgreSQL 初始化脚本
-- 适用于 Neon PostgreSQL 免费版
-- 创建时间：2026-04-20

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role SMALLINT NOT NULL DEFAULT 3,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(50),
    avatar VARCHAR(200),
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 班级表（class 是 PostgreSQL 保留字，使用 "class"）
CREATE TABLE IF NOT EXISTS "class" (
    id BIGSERIAL PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL,
    major VARCHAR(50) NOT NULL,
    grade VARCHAR(20) NOT NULL,
    teacher_id BIGINT,
    student_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 学生表
CREATE TABLE IF NOT EXISTS student (
    id BIGSERIAL PRIMARY KEY,
    student_no VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    gender SMALLINT NOT NULL,
    class_id BIGINT NOT NULL,
    major VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(50),
    id_card VARCHAR(20),
    address VARCHAR(200),
    birth_date DATE,
    enrollment_date DATE,
    status SMALLINT DEFAULT 1,
    avatar VARCHAR(200),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id BIGSERIAL PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20),
    credit DECIMAL(3,1) NOT NULL,
    teacher_id BIGINT,
    teacher_name VARCHAR(50),
    semester VARCHAR(20),
    class_hours INT,
    description TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 成绩表
CREATE TABLE IF NOT EXISTS score (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    student_no VARCHAR(20),
    student_name VARCHAR(50),
    course_id BIGINT NOT NULL,
    course_name VARCHAR(100),
    score DECIMAL(5,2),
    credit DECIMAL(3,1),
    semester VARCHAR(20),
    exam_type SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 学籍异动表
CREATE TABLE IF NOT EXISTS status_change (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    student_no VARCHAR(20),
    student_name VARCHAR(50),
    change_type SMALLINT NOT NULL,
    reason VARCHAR(500),
    from_info VARCHAR(200),
    to_info VARCHAR(200),
    status SMALLINT DEFAULT 0,
    apply_time TIMESTAMP,
    approve_time TIMESTAMP,
    approver VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 奖惩记录表
CREATE TABLE IF NOT EXISTS reward_punish (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    student_no VARCHAR(20),
    student_name VARCHAR(50),
    type SMALLINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    record_date DATE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 奖学金表
CREATE TABLE IF NOT EXISTS scholarship (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    level VARCHAR(20),
    amount DECIMAL(10,2),
    description TEXT,
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 奖学金申请表
CREATE TABLE IF NOT EXISTS scholarship_apply (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    student_no VARCHAR(20),
    student_name VARCHAR(50),
    scholarship_id BIGINT NOT NULL,
    scholarship_name VARCHAR(100),
    apply_reason TEXT,
    status SMALLINT DEFAULT 0,
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approve_time TIMESTAMP,
    approver VARCHAR(50),
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_log (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50),
    operation VARCHAR(100),
    method VARCHAR(200),
    params TEXT,
    ip VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_student_class ON student(class_id);
CREATE INDEX IF NOT EXISTS idx_student_no ON student(student_no);
CREATE INDEX IF NOT EXISTS idx_student_status ON student(status);
CREATE INDEX IF NOT EXISTS idx_score_student ON score(student_id);
CREATE INDEX IF NOT EXISTS idx_score_course ON score(course_id);
CREATE INDEX IF NOT EXISTS idx_score_semester ON score(semester);
CREATE INDEX IF NOT EXISTS idx_reward_student ON reward_punish(student_id);
CREATE INDEX IF NOT EXISTS idx_status_change_student ON status_change(student_id);
CREATE INDEX IF NOT EXISTS idx_scholarship_apply_student ON scholarship_apply(student_id);
CREATE INDEX IF NOT EXISTS idx_scholarship_apply_status ON scholarship_apply(status);
CREATE INDEX IF NOT EXISTS idx_sys_user_role ON sys_user(role);
CREATE INDEX IF NOT EXISTS idx_sys_log_username ON sys_log(username);
CREATE INDEX IF NOT EXISTS idx_sys_log_createtime ON sys_log(create_time);
