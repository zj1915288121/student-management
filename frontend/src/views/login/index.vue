<template>
  <div class="login-container">
    <!-- Left Brand Panel -->
    <div class="login-left">
      <div class="left-content">
        <div class="brand-icon">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/>
          </svg>
        </div>
        <h1 class="brand-title">学生信息管理系统</h1>
        <p class="brand-subtitle">Student Information Management System</p>
        <div class="feature-list">
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>多角色权限管理</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>成绩分析与预警</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>学籍异动在线审批</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>奖学金申请管理</span>
          </div>
        </div>
      </div>
      <div class="left-decoration">
        <div class="deco-circle deco-1"></div>
        <div class="deco-circle deco-2"></div>
        <div class="deco-circle deco-3"></div>
      </div>
    </div>

    <!-- Right Login Panel -->
    <div class="login-right">
      <div class="login-card">
        <div class="login-header">
          <h2 class="login-title">欢迎回来</h2>
          <p class="login-desc">请输入您的账号信息登录系统</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          size="large"
          @submit.prevent
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              <span v-if="loading">登录中...</span>
              <span v-else>登 录</span>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <div class="demo-accounts">
            <span class="demo-label">演示账号</span>
            <div class="demo-tags">
              <button
                class="demo-tag"
                @click="fillAccount('admin', '123456')"
                type="button"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                管理员
              </button>
              <button
                class="demo-tag"
                @click="fillAccount('T001', '123456')"
                type="button"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
                教师
              </button>
              <button
                class="demo-tag"
                @click="fillAccount('20240001', '123456')"
                type="button"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                学生
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const fillAccount = (username, password) => {
  loginForm.username = username
  loginForm.password = password
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.loginAction(loginForm)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ===== Design Tokens ===== */
.login-container {
  --c-primary: #2563eb;
  --c-primary-light: #3b82f6;
  --c-primary-dark: #1d4ed8;
  --c-accent: #059669;
  --c-bg: #f8fafc;
  --c-fg: #0f172a;
  --r-sm: 6px;
  --r-md: 8px;
  --r-lg: 12px;
  --r-xl: 16px;
  --r-2xl: 20px;
  --shadow-xs: 0 1px 2px rgba(0,0,0,0.05);
  --shadow-sm: 0 1px 3px rgba(0,0,0,0.1), 0 1px 2px rgba(0,0,0,0.06);
  --shadow-md: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06);
  --shadow-lg: 0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -2px rgba(0,0,0,0.05);
  --shadow-xl: 0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04);
  --t-fast: 150ms;
  --t-normal: 200ms;
  --t-slow: 300ms;
}

.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  overflow: hidden;
  font-family: 'Fira Sans', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* ===== Left Brand Panel ===== */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 60px;
}

.left-content {
  position: relative;
  z-index: 2;
  color: #f1f5f9;
  max-width: 460px;
}

.brand-icon {
  width: 76px;
  height: 76px;
  background: linear-gradient(135deg, var(--c-primary) 0%, var(--c-primary-light) 100%);
  border-radius: var(--r-2xl);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-bottom: 32px;
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.4);
}

.brand-title {
  font-size: 34px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 8px;
  letter-spacing: 1px;
  line-height: 1.2;
}

.brand-subtitle {
  font-size: 13px;
  color: #64748b;
  letter-spacing: 2px;
  margin-bottom: 48px;
  text-transform: uppercase;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  color: #cbd5e1;
  transition: color var(--t-normal) ease;
}

.feature-item:hover {
  color: #f1f5f9;
}

.feature-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--c-primary-light);
  flex-shrink: 0;
  box-shadow: 0 0 10px rgba(59, 130, 246, 0.5);
}

/* ===== Left Decoration ===== */
.left-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(59, 130, 246, 0.08);
}

.deco-1 {
  width: 500px;
  height: 500px;
  bottom: -200px;
  right: -150px;
}

.deco-2 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -80px;
}

.deco-3 {
  width: 150px;
  height: 150px;
  top: 30%;
  right: 10%;
  background: rgba(37, 99, 235, 0.04);
}

/* ===== Right Login Panel ===== */
.login-right {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  padding: 60px;
}

.login-card {
  width: 100%;
  max-width: 380px;
}

.login-header {
  margin-bottom: 36px;
}

.login-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--c-fg);
  margin-bottom: 8px;
}

.login-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.5;
}

/* ===== Form Styles ===== */
.login-form :deep(.el-input__wrapper) {
  border-radius: var(--r-md);
  padding: 4px 12px;
  box-shadow: 0 0 0 1px #e2e8f0 inset;
  transition: box-shadow var(--t-normal) ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #94a3b8 inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--c-primary) inset, 0 0 0 4px rgba(37, 99, 235, 0.1) !important;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.login-form :deep(.el-form-item__error) {
  font-size: 12px;
  padding-top: 4px;
}

/* ===== Login Button ===== */
.login-btn {
  width: 100%;
  border-radius: var(--r-md);
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, var(--c-primary) 0%, var(--c-primary-light) 100%);
  border: none;
  transition: all var(--t-normal) ease;
  cursor: pointer;
}

.login-btn:hover {
  background: linear-gradient(135deg, var(--c-primary-dark) 0%, var(--c-primary) 100%);
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.4);
  transform: translateY(-1px);
}

.login-btn:active {
  transform: translateY(0);
}

.login-btn:focus-visible {
  outline: 2px solid var(--c-primary);
  outline-offset: 2px;
}

/* ===== Demo Accounts ===== */
.login-footer {
  margin-top: 28px;
  text-align: center;
}

.demo-accounts {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.demo-label {
  font-size: 12px;
  color: #94a3b8;
  letter-spacing: 0.5px;
}

.demo-tags {
  display: flex;
  gap: 8px;
}

.demo-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: var(--r-sm);
  font-size: 12px;
  font-weight: 500;
  color: #64748b;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all var(--t-normal) ease;
  outline: none;
  line-height: 1.6;
}

.demo-tag:hover {
  color: var(--c-primary);
  border-color: var(--c-primary-light);
  background: #eff6ff;
}

.demo-tag:focus-visible {
  outline: 2px solid var(--c-primary);
  outline-offset: 2px;
}

.demo-tag svg {
  flex-shrink: 0;
}

/* ===== Responsive ===== */
@media (max-width: 960px) {
  .login-left {
    display: none;
  }
  .login-right {
    width: 100%;
  }
}

/* ===== Reduced Motion ===== */
@media (prefers-reduced-motion: reduce) {
  .login-btn {
    transition: none;
  }

  .login-form :deep(.el-input__wrapper) {
    transition: none;
  }

  .feature-item {
    transition: none;
  }

  .demo-tag {
    transition: none;
  }
}
</style>
