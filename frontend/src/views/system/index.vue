<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">
              <SvgIcon name="settings" :size="20" :stroke-width="2" style="color: var(--color-primary)" />
            </span>
            <span>系统管理</span>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="custom-tabs">
        <!-- 用户管理 -->
        <el-tab-pane name="user">
          <template #label>
            <span class="tab-label">
              <SvgIcon name="users" :size="14" :stroke-width="2" />
              用户管理
            </span>
          </template>

          <!-- 角色统计卡片 -->
          <div class="stat-cards">
            <div class="stat-card" v-for="role in roles" :key="role.value">
              <el-card shadow="hover" class="stat-inner-card">
                <div class="stat-content">
                  <div class="stat-info">
                    <div class="stat-label">{{ role.label }}</div>
                    <div class="stat-value">{{ role.count }}</div>
                  </div>
                  <div class="stat-icon" :style="{ background: role.gradient }">
                    <SvgIcon v-if="role.value === 1" name="shield" :size="24" :stroke-width="2" />
                    <SvgIcon v-else-if="role.value === 2" name="user" :size="24" :stroke-width="2" />
                    <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/><line x1="12" y1="11" x2="12" y2="17"/><line x1="9" y1="14" x2="15" y2="14"/></svg>
                  </div>
                </div>
                <div class="stat-desc">{{ role.desc }}</div>
              </el-card>
            </div>
          </div>

          <div class="search-bar">
            <el-input v-model="userQuery.keyword" placeholder="搜索用户名/姓名" clearable class="search-input" @keyup.enter="loadUsers">
              <template #prefix>
                <SvgIcon name="search" :size="14" :stroke-width="2" style="color: var(--color-text-muted)" />
              </template>
            </el-input>
            <el-select v-model="userQuery.role" placeholder="角色" clearable class="search-select" @change="loadUsers">
              <el-option label="管理员" :value="1" /><el-option label="教师" :value="2" /><el-option label="学生" :value="3" />
            </el-select>
            <el-select v-model="userQuery.status" placeholder="状态" clearable class="search-select-sm" @change="loadUsers">
              <el-option label="正常" :value="1" /><el-option label="禁用" :value="0" />
            </el-select>
            <el-button type="primary" @click="loadUsers">
              <SvgIcon name="search" :size="14" :stroke-width="2" />
              搜索
            </el-button>
            <el-button @click="userQuery.keyword='';userQuery.role=null;userQuery.status=null;loadUsers">
              <SvgIcon name="reset" :size="14" :stroke-width="2" />
              重置
            </el-button>
            <el-button type="primary" @click="handleAddUser">
              <SvgIcon name="plus" :size="14" :stroke-width="2.5" />
              新增用户
            </el-button>
          </div>

          <el-table :data="userData" v-loading="userLoading" stripe class="data-table">
            <template #empty><el-empty description="暂无用户数据" /></template>
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column label="角色" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="roleTagType(row.role)" size="small">{{ row.roleName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="手机" width="130" />
            <el-table-column prop="email" label="邮箱" min-width="160" />
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-switch
                  :model-value="row.status === 1"
                  @change="handleToggleUser(row)"
                  :disabled="row.role === 1"
                  inline-prompt
                  active-text="启"
                  inactive-text="禁"
                  class="status-switch"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="240" fixed="right" align="center">
              <template #default="{ row }">
                <el-button type="primary" link size="small" class="action-link" @click="handleEditUser(row)">
                  <SvgIcon name="edit" :size="13" :stroke-width="2" />
                  编辑
                </el-button>
                <el-button type="warning" link size="small" class="action-link" @click="handleResetPwd(row)">
                  <SvgIcon name="lock" :size="13" :stroke-width="2" />
                  重置密码
                </el-button>
                <el-button v-if="row.role !== 1" type="danger" link size="small" class="action-link" @click="handleDeleteUser(row)">
                  <SvgIcon name="delete" :size="13" :stroke-width="2" />
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrap">
            <el-pagination v-model:current-page="userQuery.page" v-model:page-size="userQuery.size" :total="userTotal"
              :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @size-change="loadUsers" @current-change="loadUsers" />
          </div>
        </el-tab-pane>

        <!-- 操作日志 -->
        <el-tab-pane name="log">
          <template #label>
            <span class="tab-label">
              <SvgIcon name="document" :size="14" :stroke-width="2" />
              操作日志
            </span>
          </template>
          <div class="search-bar">
            <el-input v-model="logQuery.keyword" placeholder="搜索用户名/操作" clearable class="search-input" @keyup.enter="loadLogs">
              <template #prefix>
                <SvgIcon name="search" :size="14" :stroke-width="2" style="color: var(--color-text-muted)" />
              </template>
            </el-input>
            <el-date-picker v-model="logDateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" class="search-daterange" @change="handleLogDateChange" />
            <el-button type="primary" @click="loadLogs">
              <SvgIcon name="search" :size="14" :stroke-width="2" />
              搜索
            </el-button>
            <el-button @click="logQuery.keyword='';logQuery.dateStart='';logQuery.dateEnd='';logDateRange=null;loadLogs">
              <SvgIcon name="reset" :size="14" :stroke-width="2" />
              重置
            </el-button>
          </div>
          <el-table :data="logData" v-loading="logLoading" stripe class="data-table">
            <template #empty><el-empty description="暂无操作日志" /></template>
            <el-table-column prop="username" label="操作用户" width="120" />
            <el-table-column prop="operation" label="操作" width="140" />
            <el-table-column prop="method" label="方法" min-width="200" show-overflow-tooltip />
            <el-table-column prop="ip" label="IP" width="130" />
            <el-table-column prop="time" label="耗时(ms)" width="100" align="right" />
            <el-table-column prop="createTime" label="操作时间" width="170" />
          </el-table>
          <div class="pagination-wrap">
            <el-pagination v-model:current-page="logQuery.page" v-model:page-size="logQuery.size" :total="logTotal"
              :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @size-change="loadLogs" @current-change="loadLogs" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 用户编辑弹窗 -->
    <el-dialog v-model="userDialogVisible" :title="userDialogTitle" width="520px" destroy-on-close class="custom-dialog">
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="80px" class="dialog-form">
        <el-form-item label="用户名" prop="username"><el-input v-model="userForm.username" placeholder="请输入用户名" /></el-form-item>
        <el-form-item v-if="!editUserId" label="密码" prop="password"><el-input v-model="userForm.password" type="password" placeholder="默认123456" /></el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="userForm.role">
            <el-radio :value="1">管理员</el-radio><el-radio :value="2">教师</el-radio><el-radio :value="3">学生</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="姓名" prop="realName"><el-input v-model="userForm.realName" placeholder="请输入姓名" /></el-form-item>
        <el-form-item label="手机" prop="phone"><el-input v-model="userForm.phone" placeholder="请输入手机号" /></el-form-item>
        <el-form-item label="邮箱" prop="email"><el-input v-model="userForm.email" placeholder="请输入邮箱" /></el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="userDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleUserSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPageList as getUserPageList, add as addUser, update as updateUser, remove as removeUser, resetPassword, toggleStatus, getRoleCounts } from '@/api/user'
import { getPageList as getLogPageList } from '@/api/log'

const activeTab = ref('user')
const submitLoading = ref(false)

const userLoading = ref(false); const userData = ref([]); const userTotal = ref(0)
const userQuery = reactive({ keyword: '', role: null, status: null, page: 1, size: 10 })
const userDialogVisible = ref(false); const userDialogTitle = ref('新增用户'); const editUserId = ref(null)
const userForm = reactive({ username: '', password: '', role: 2, realName: '', phone: '', email: '' })
const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
}
const userFormRef = ref(null)

const roles = ref([
  { value: 1, label: '管理员', gradient: 'linear-gradient(135deg, #dc2626 0%, #b91c1c 100%)', desc: '拥有系统全部权限，可管理所有模块和用户', count: 0 },
  { value: 2, label: '教师', gradient: 'linear-gradient(135deg, #d97706 0%, #b45309 100%)', desc: '可管理学生信息、成绩、课程、学籍异动等', count: 0 },
  { value: 3, label: '学生', gradient: 'linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)', desc: '可查看个人成绩、申请奖学金、提交异动申请', count: 0 }
])

const logLoading = ref(false); const logData = ref([]); const logTotal = ref(0)
const logQuery = reactive({ keyword: '', dateStart: '', dateEnd: '', page: 1, size: 10 })
const logDateRange = ref(null)

const roleTagType = (r) => ({ 1: 'danger', 2: 'warning', 3: '' }[r] || 'info')

const loadUsers = async () => { userLoading.value = true; try { const res = await getUserPageList(userQuery); userData.value = res.data.records; userTotal.value = res.data.total } finally { userLoading.value = false } }

const loadRoleCounts = async () => { try { const res = await getRoleCounts(); roles.value[0].count = res.data[1] || 0; roles.value[1].count = res.data[2] || 0; roles.value[2].count = res.data[3] || 0 } catch {} }

const loadLogs = async () => { logLoading.value = true; try { const res = await getLogPageList(logQuery); logData.value = res.data.records; logTotal.value = res.data.total } finally { logLoading.value = false } }
const handleLogDateChange = (val) => { logQuery.dateStart = val ? val[0] : ''; logQuery.dateEnd = val ? val[1] : ''; loadLogs() }

const handleAddUser = () => { userDialogTitle.value = '新增用户'; editUserId.value = null; Object.assign(userForm, { username: '', password: '', role: 2, realName: '', phone: '', email: '' }); userDialogVisible.value = true }
const handleEditUser = (row) => { userDialogTitle.value = '编辑用户'; editUserId.value = row.id; Object.assign(userForm, { username: row.username, password: '', role: row.role, realName: row.realName, phone: row.phone, email: row.email }); userDialogVisible.value = true }
const handleUserSubmit = async () => { const v = await userFormRef.value.validate().catch(() => false); if (!v) return; submitLoading.value = true; try { if (editUserId.value) { await updateUser(editUserId.value, userForm); ElMessage.success('修改成功') } else { await addUser(userForm); ElMessage.success('新增成功') } userDialogVisible.value = false; loadUsers() } finally { submitLoading.value = false } }
const handleResetPwd = (row) => { ElMessageBox.confirm(`确定重置"${row.realName}"的密码为123456吗？`, '提示', { type: 'warning' }).then(async () => { await resetPassword(row.id); ElMessage.success('密码已重置为123456') }).catch(() => {}) }
const handleToggleUser = async (row) => { await toggleStatus(row.id); ElMessage.success(row.status === 1 ? '已禁用' : '已启用'); loadUsers() }
const handleDeleteUser = (row) => { ElMessageBox.confirm(`确定删除用户"${row.realName}"吗？`, '提示', { type: 'warning' }).then(async () => { await removeUser(row.id); ElMessage.success('删除成功'); loadUsers() }).catch(() => {}) }

onMounted(() => { loadUsers(); loadLogs(); loadRoleCounts() })
</script>

<style scoped>
.search-input { width: 200px; }
.search-select { width: 120px; }
.search-select-sm { width: 100px; }
.search-daterange { width: 280px; }

.data-table { width: 100%; }

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-inner-card {
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  transition: all var(--transition-normal);
  cursor: default;
}
.stat-inner-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.stat-inner-card :deep(.el-card__body) { padding: 20px; }

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-info { flex: 1; }

.stat-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 4px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  font-variant-numeric: tabular-nums;
  line-height: 1.2;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-on-primary);
  flex-shrink: 0;
}

.stat-desc {
  margin-top: 12px;
  font-size: 12px;
  color: var(--color-text-muted);
  line-height: 1.5;
}

/* Tab 样式 */
.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.tab-label svg { vertical-align: -1px; }

/* 操作链接 */
.action-link {
  cursor: pointer;
  transition: opacity var(--transition-fast);
}
.action-link:hover { opacity: 0.8; }
.action-link svg { vertical-align: -1px; margin-right: 2px; }

/* 状态开关 */
.status-switch {
  --el-switch-on-color: var(--color-accent);
  --el-switch-off-color: var(--color-text-muted);
}

.dialog-form { padding: 4px 0; }

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.search-bar :deep(.el-input__prefix) {
  display: flex;
  align-items: center;
  padding-left: 4px;
}

.custom-tabs :deep(.el-tabs__item) {
  font-weight: 500;
  transition: color var(--transition-fast);
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
}

@media (prefers-reduced-motion: reduce) {
  .action-link { transition: none; }
  .stat-inner-card { transition: none; }
  .stat-inner-card:hover { transform: none; }
}
</style>
