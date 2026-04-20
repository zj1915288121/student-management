# 免费上线部署指南

**学生信息管理系统 - Railway + Neon PostgreSQL + Vercel 免费部署**

---

## 部署架构

```
用户浏览器
    ↓ HTTPS
Vercel (前端)  →  https://student-management.vercel.app
    ↓ API 请求
Railway (后端) →  https://student-api-xxx.up.railway.app
    ↓
Neon PostgreSQL →  免费 0.5GB 数据库
```

---

## 第一步：创建各平台账号（10分钟）

### 1.1 Neon（数据库）
1. 打开 https://neon.tech
2. 点击 "Sign in with GitHub"（用 GitHub 账号登录）
3. 点击 "New Project"
   - Project name: `student-management`
   - Region: Singapore
   - PostgreSQL version: 15
4. 创建完成后，复制 **Connection string**，格式如下：
   ```
   postgresql://用户名:密码@ep-xxx-xxx-123456.singapore-postgres.pooler.supabase.com:6543/student_management
   ```
   **保存好，稍后会用到**

### 1.2 Railway（后端）
1. 打开 https://railway.app
2. 点击 "Sign in with GitHub"（用同一个 GitHub 账号登录）
3. 授权 GitHub 访问（如果需要）
4. 暂时不创建项目，等代码推送后 Railway 会自动检测

### 1.3 Vercel（前端）
1. 打开 https://vercel.com
2. 点击 "Sign up" → 用 GitHub 账号登录
3. 授权 GitHub 仓库访问

---

## 第二步：代码已推送（已由 AI/Trae 完成）

代码已推送到 GitHub：`https://github.com/zj1915288121/student-management`

包含的改动：
- ✅ MySQL → PostgreSQL（pom.xml + application.yml）
- ✅ `class` 表名加双引号（PostgreSQL 保留字）
- ✅ 创建 Dockerfile
- ✅ 创建 railway.toml
- ✅ 创建 vercel.json
- ✅ 创建 .env.production
- ✅ 生成 init-pg.sql 和 test_data-pg.sql

---

## 第三步：Railway 部署后端

### 3.1 从 GitHub 创建 Railway 项目
1. 打开 https://railway.app/dashboard
2. 点击 "New Project" → "Deploy from GitHub repo"
3. 选择 `zj1915288121/student-management` 仓库
4. Railway 会检测到 Dockerfile，自动构建

### 3.2 配置数据库连接
1. 在 Railway 项目中，点击 "Variables" 标签
2. 点击 "New Variable"，添加：
   - **Variable name**: `DATABASE_URL`
   - **Value**: 粘贴 Neon 的 Connection string（去掉引号）

   示例值：
   ```
   postgresql://username:password@ep-xxx-xxx-123456.singapore-postgres.pooler.supabase.com:6543/student_management
   ```

3. 再添加一个：
   - **Variable name**: `JWT_SECRET`
   - **Value**: `YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkw`
   （这是默认测试密钥，生产环境请生成新的随机字符串）

4. 点击 "Deploy" 触发部署

### 3.3 等待构建（约 5-8 分钟）
- Railway 会自动构建 Docker 镜像
- 冷启动约 30-60 秒（Java Spring Boot）
- 部署成功后，复制 **自定义域名**（或使用 Railway 提供的随机域名）

### 3.4 初始化数据库
1. 在 Neon 控制台，点击项目 → "SQL Editor"
2. 依次执行两个 SQL 文件：
   - 复制 `database/init-pg.sql` 的内容，粘贴执行
   - 复制 `database/test_data-pg.sql` 的内容，粘贴执行
3. 执行成功后，数据库初始化完成

---

## 第四步：Vercel 部署前端

### 4.1 从 GitHub 导入
1. 打开 https://vercel.com/new
2. 点击 "Import Git Repository"
3. 选择 `zj1915288121/student-management`
4. Framework Preset: `Vite`
5. Root Directory: `frontend`
6. 点击 "Deploy"

### 4.2 配置环境变量（可选）
- `VITE_API_URL`: 填入 Railway 的后端地址
  - 例如：`https://student-api-xxx.up.railway.app`
  - 如果不填，前端默认请求 `/api`（通过 Vercel 代理）

### 4.3 等待部署（约 1-2 分钟）
- Vercel 部署成功后，给一个 `.vercel.app` 域名
- 例如：`https://student-management-xxx.vercel.app`

---

## 第五步：测试上线

部署完成后，访问前端地址测试：

### 测试账号
| 角色 | 账号 | 密码 |
|:---:|:---:|:---:|
| 管理员 | admin | 123456 |
| 教师 | T001 | 123456 |
| 学生 | 20240001 | 123456 |

### 测试流程
1. 打开前端地址（Vercel 给的 URL）
2. 点击 "管理员" 快捷登录 → 自动填充 `admin/123456`
3. 点击登录，应该能进入 Dashboard
4. 测试各功能模块（学生/成绩/课程/奖惩等）

---

## 第六步：自定义域名（可选）

### Railway 自定义域名
1. Railway 项目 → Settings → Networking
2. 点击 "Add Custom Domain"
3. 填入自己的域名，按提示配置 DNS

### Vercel 自定义域名
1. 项目 → Settings → Domains
2. 填入域名，按 Vercel 给的 DNS 配置说明配置

---

## 注意事项

### 免费版限制
| 服务 | 限制 | 说明 |
|:---|:---|:---|
| Railway | $5/月额度 | 休眠后首次访问约 30 秒冷启动 |
| Neon | 0.5GB 存储 | 45 个学生数据完全够用 |
| Vercel | 100GB 带宽/月 | 访问量不大完全够用 |

### 冷启动说明
- Railway 免费版如果 1 小时无请求，实例会休眠
- 首次访问会触发冷启动，约 30-60 秒后恢复正常
- 这对演示项目是可接受的

### 安全提示
- 生产环境请更换 `JWT_SECRET` 为随机字符串（>= 64 字符）
- 生产环境请更换数据库密码
- 建议启用 HTTPS（Railway 和 Vercel 默认启用）

---

## 故障排查

### 登录页报错 "网络连接异常"
- 检查 Railway 后端是否已启动
- 检查 DATABASE_URL 是否正确
- 检查 Railway 日志是否有错误

### 登录后显示"系统异常"
- 检查 Neon 数据库是否初始化（init-pg.sql + test_data-pg.sql）
- 检查 Railway 日志中的具体错误

### 页面空白或白屏
- 检查浏览器控制台（F12）
- 检查 Vercel 部署是否成功

---

**有问题随时告诉我，帮你排查！** 🚀
