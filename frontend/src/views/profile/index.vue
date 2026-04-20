<template>
  <div class="profile-container">
    <el-row :gutter="16">
      <!-- Left Card: Profile Info -->
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="profile-avatar-wrap">
            <div class="profile-avatar">
              {{ (profile.realName || profile.username || '用').charAt(0) }}
            </div>
          </div>
          <h2 class="profile-name">{{ profile.realName || profile.username }}</h2>
          <div class="profile-role-wrap">
            <el-tag :type="roleTagType" effect="dark" round class="role-tag">{{ profile.roleName }}</el-tag>
          </div>
          <div class="profile-info-list">
            <div class="info-item">
              <span class="info-icon">
                <SvgIcon name="user" :size="16" :stroke-width="2" style="color: var(--color-primary)" />
              </span>
              <span class="info-label">用户名</span>
              <span class="info-value">{{ profile.username }}</span>
            </div>
            <div class="info-item">
              <span class="info-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--color-accent)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
                </svg>
              </span>
              <span class="info-label">手机</span>
              <span class="info-value">{{ profile.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--color-warning)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" /><polyline points="22,6 12,13 2,6" />
                </svg>
              </span>
              <span class="info-label">邮箱</span>
              <span class="info-value">{{ profile.email || '未设置' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Right Card: Tabs -->
      <el-col :span="16">
        <el-card class="form-card">
          <el-tabs v-model="activeTab" class="profile-tabs">
            <!-- Info Tab -->
            <el-tab-pane name="info">
              <template #label>
                <span class="tab-label">
                  <SvgIcon name="edit" :size="14" :stroke-width="2" />
                  修改信息
                </span>
              </template>
              <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="80px" class="info-form">
                <el-form-item label="姓名">
                  <el-input v-model="infoForm.realName" placeholder="请输入姓名" />
                </el-form-item>
                <el-form-item label="手机" prop="phone">
                  <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="infoLoading" class="submit-btn" @click="handleUpdateInfo">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z" /><polyline points="17 21 17 13 7 13 7 21" /><polyline points="7 3 7 8 15 8" /></svg>
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- Password Tab -->
            <el-tab-pane name="password">
              <template #label>
                <span class="tab-label">
                  <SvgIcon name="lock" :size="14" :stroke-width="2" />
                  修改密码
                </span>
              </template>
              <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px" class="pwd-form">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请确认新密码" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="pwdLoading" class="submit-btn" @click="handleChangePwd">
                    <SvgIcon name="lock" :size="14" :stroke-width="2" />
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile, changePassword } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const activeTab = ref('info')
const profile = ref({})
const infoLoading = ref(false)
const pwdLoading = ref(false)
const infoFormRef = ref(null)
const pwdFormRef = ref(null)

const infoForm = reactive({ realName: '', phone: '', email: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const infoRules = {
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) callback(new Error('两次密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

const roleTagType = computed(() => ({ 1: 'danger', 2: 'warning', 3: '' }[profile.value.role] || 'info'))

const loadProfile = async () => {
  try {
    const res = await getProfile()
    profile.value = res.data
    Object.assign(infoForm, { realName: res.data.realName, phone: res.data.phone, email: res.data.email })
  } catch {}
}

const handleUpdateInfo = async () => {
  const v = await infoFormRef.value.validate().catch(() => false)
  if (!v) return
  infoLoading.value = true
  try {
    await updateProfile(infoForm)
    ElMessage.success('修改成功')
    loadProfile()
  } finally {
    infoLoading.value = false
  }
}

const handleChangePwd = async () => {
  const v = await pwdFormRef.value.validate().catch(() => false)
  if (!v) return
  pwdLoading.value = true
  try {
    await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logoutAction()
  } finally {
    pwdLoading.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.profile-container {
  padding: 0;
}

/* Profile Card */
.profile-card {
  text-align: center;
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition-normal);
}

.profile-card:hover {
  box-shadow: var(--shadow-md);
}

.profile-card :deep(.el-card__body) {
  padding: 32px 24px;
}

.profile-avatar-wrap {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-2xl);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  color: var(--color-on-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.3);
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
}

.profile-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.4);
}

.profile-name {
  font-size: 20px;
  color: var(--color-foreground);
  margin: 12px 0 8px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.profile-role-wrap {
  margin-bottom: 8px;
}

.role-tag {
  font-weight: 500;
  letter-spacing: 0.5px;
}

.profile-info-list {
  margin-top: 24px;
  text-align: left;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 0;
  border-bottom: 1px solid var(--color-muted);
  transition: background-color var(--transition-fast);
}

.info-item:last-child {
  border-bottom: none;
}

.info-item:hover {
  background-color: rgba(37, 99, 235, 0.02);
  border-radius: var(--radius-sm);
}

.info-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: var(--radius-sm);
  background: rgba(37, 99, 235, 0.06);
  flex-shrink: 0;
}

.info-label {
  font-size: 13px;
  color: var(--color-text-muted);
  min-width: 48px;
}

.info-value {
  font-size: 14px;
  color: var(--color-text-primary);
  font-weight: 500;
}

/* Form Card */
.form-card {
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition-normal);
}

.form-card:hover {
  box-shadow: var(--shadow-md);
}

/* Tabs */
.profile-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.profile-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: var(--color-border);
}

.profile-tabs :deep(.el-tabs__active-bar) {
  background-color: var(--color-primary);
  border-radius: 2px;
}

.profile-tabs :deep(.el-tabs__item) {
  font-weight: 500;
  color: var(--color-text-secondary);
  transition: color var(--transition-fast);
}

.profile-tabs :deep(.el-tabs__item.is-active) {
  color: var(--color-primary);
  font-weight: 600;
}

.profile-tabs :deep(.el-tabs__item:hover) {
  color: var(--color-primary);
}

.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

/* Forms */
.info-form,
.pwd-form {
  max-width: 500px;
  margin-top: 24px;
}

.submit-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border-radius: var(--radius-md);
  font-weight: 500;
  transition: all var(--transition-fast);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

/* Reduced Motion */
@media (prefers-reduced-motion: reduce) {
  .profile-card,
  .form-card,
  .profile-avatar,
  .info-item,
  .submit-btn {
    transition: none;
  }
  .profile-avatar:hover {
    transform: none;
  }
  .submit-btn:hover {
    transform: none;
  }
}
</style>
