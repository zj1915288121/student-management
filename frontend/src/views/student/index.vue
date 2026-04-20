<template>
  <div class="page-container">
    <!-- Stats Cards -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">学生总数</p>
              <p class="stat-value">{{ stats.total || 0 }}</p>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light))">
              <SvgIcon name="user" :size="22" :stroke-width="2" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">在校</p>
              <p class="stat-value">{{ stats.active || 0 }}</p>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, var(--color-accent), var(--color-accent-light))">
              <SvgIcon name="check" :size="22" :stroke-width="2" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">休学</p>
              <p class="stat-value">{{ stats.suspended || 0 }}</p>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, #d97706, #f59e0b)">
              <SvgIcon name="warning" :size="22" :stroke-width="2" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">毕业</p>
              <p class="stat-value">{{ stats.graduated || 0 }}</p>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, #7c3aed, #8b5cf6)">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z" /><path d="M6 12v5c3 3 9 3 12 0v-5" /></svg>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Main Card -->
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">
              <SvgIcon name="user" :size="20" :stroke-width="2" style="color: var(--color-primary)" />
            </span>
            <span class="title-text">学生管理</span>
          </div>
          <div class="header-btns">
            <el-button type="primary" class="action-btn" @click="handleAdd">
              <SvgIcon name="plus" :size="14" :stroke-width="2.5" />
              <span>新增</span>
            </el-button>
            <el-button type="success" class="action-btn" @click="handleImport">
              <SvgIcon name="upload" :size="14" :stroke-width="2" />
              <span>导入</span>
            </el-button>
            <el-button type="warning" class="action-btn" @click="handleExport">
              <SvgIcon name="download" :size="14" :stroke-width="2" />
              <span>导出</span>
            </el-button>
            <el-button type="danger" class="action-btn" :disabled="!selectedIds.length" @click="handleBatchDelete">
              <SvgIcon name="delete" :size="14" :stroke-width="2" />
              <span>批量删除</span>
            </el-button>
          </div>
        </div>
      </template>

      <!-- Search Bar -->
      <div class="search-bar">
        <el-input
          v-model="query.keyword"
          placeholder="学号/姓名"
          clearable
          class="search-input"
          @keyup.enter="loadData"
        >
          <template #prefix>
            <SvgIcon name="search" :size="14" :stroke-width="2" style="color: var(--color-text-muted)" />
          </template>
        </el-input>
        <el-select v-model="query.classId" placeholder="班级" clearable class="search-select" @change="loadData">
          <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
        </el-select>
        <el-select v-model="query.status" placeholder="状态" clearable class="search-select short" @change="loadData">
          <el-option label="在校" :value="1" />
          <el-option label="休学" :value="2" />
          <el-option label="毕业" :value="3" />
          <el-option label="退学" :value="4" />
        </el-select>
        <el-button type="primary" class="search-btn" @click="loadData">
          <SvgIcon name="search" :size="14" :stroke-width="2" />
          <span>搜索</span>
        </el-button>
        <el-button class="reset-btn" @click="resetQuery">
          <SvgIcon name="reset" :size="14" :stroke-width="2" />
          <span>重置</span>
        </el-button>
      </div>

      <!-- Table -->
      <el-table :data="tableData" v-loading="loading" stripe class="data-table" @selection-change="handleSelectionChange">
        <template #empty>
          <el-empty description="暂无学生数据">
            <template #image>
              <SvgIcon name="user" :size="120" :stroke-width="0.8" style="color: var(--color-text-muted)" />
            </template>
          </el-empty>
        </template>
        <el-table-column type="selection" width="50" />
        <el-table-column prop="studentNo" label="学号" width="120">
          <template #default="{ row }">
            <span class="cell-mono">{{ row.studentNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="100">
          <template #default="{ row }">
            <span class="cell-primary">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === 1 ? '' : 'danger'" size="small" effect="plain" round class="gender-tag">{{ row.gender === 1 ? '男' : '女' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="150" />
        <el-table-column prop="major" label="专业" min-width="140" />
        <el-table-column prop="phone" label="手机" width="130">
          <template #default="{ row }">
            <span class="cell-mono">{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small" effect="plain" round>{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" class="op-btn" @click="handleEdit(row)">
              <SvgIcon name="edit" :size="13" :stroke-width="2" />
              编辑
            </el-button>
            <el-button type="danger" link size="small" class="op-btn" @click="handleDelete(row)">
              <SvgIcon name="delete" :size="13" :stroke-width="2" />
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close class="form-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="classId">
              <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%">
                <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="专业">
              <el-input v-model="form.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入学日期">
              <el-date-picker v-model="form.enrollmentDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="家庭住址">
          <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入家庭住址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPageList, add, update, remove, batchRemove, getStats, importExcel, exportExcel } from '@/api/student'
import { getAll as getAllClasses } from '@/api/classes'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增学生')
const editId = ref(null)
const tableData = ref([])
const total = ref(0)
const selectedIds = ref([])
const classList = ref([])
const stats = ref({})
const formRef = ref(null)

const query = reactive({ keyword: '', classId: null, status: null, page: 1, size: 10 })
const form = reactive({ studentNo: '', name: '', gender: 1, classId: null, major: '', phone: '', email: '', enrollmentDate: '', address: '' })
const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
}

const statusTagType = (s) => ({ 1: 'success', 2: 'warning', 3: '', 4: 'info' }[s] || 'info')
const statusText = (s) => ({ 1: '在校', 2: '休学', 3: '毕业', 4: '退学' }[s] || '未知')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPageList(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try { const res = await getStats(); stats.value = res.data } catch {}
}

const loadClasses = async () => {
  try { const res = await getAllClasses(); classList.value = res.data } catch {}
}

const resetQuery = () => {
  query.keyword = ''
  query.classId = null
  query.status = null
  query.page = 1
  loadData()
}

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map(r => r.id)
}

const handleAdd = () => {
  dialogTitle.value = '新增学生'
  editId.value = null
  Object.assign(form, { studentNo: '', name: '', gender: 1, classId: null, major: '', phone: '', email: '', enrollmentDate: '', address: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑学生'
  editId.value = row.id
  Object.assign(form, { studentNo: row.studentNo, name: row.name, gender: row.gender, classId: row.classId, major: row.major, phone: row.phone, email: row.email, enrollmentDate: row.enrollmentDate, address: row.address })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (editId.value) {
      await update(editId.value, form)
      ElMessage.success('修改成功')
    } else {
      await add(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
    loadStats()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除学生"${row.name}"吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await remove(row.id)
      ElMessage.success('删除成功')
      loadData()
      loadStats()
    })
    .catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定删除选中的${selectedIds.value.length}个学生吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await batchRemove(selectedIds.value.join(','))
      ElMessage.success('删除成功')
      loadData()
      loadStats()
    })
    .catch(() => {})
}

const handleImport = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    try {
      const res = await importExcel(file)
      ElMessage.success(`导入完成：成功${res.data.successCount}条，失败${res.data.failCount}条`)
      loadData()
      loadStats()
    } catch {}
  }
  input.click()
}

const handleExport = async () => {
  try {
    const res = await exportExcel(query)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'students.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
  } catch {}
}

onMounted(() => { loadData(); loadStats(); loadClasses() })
</script>

<style scoped>
.main-card {
  margin-top: 16px;
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition-normal);
}

.main-card:hover {
  box-shadow: var(--shadow-md);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}

.title-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  background: rgba(37, 99, 235, 0.08);
}

.title-text {
  font-size: 16px;
  color: var(--color-foreground);
  letter-spacing: 0.3px;
}

.header-btns {
  display: flex;
  gap: 8px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  border-radius: var(--radius-md);
  font-weight: 500;
  transition: all var(--transition-fast);
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

/* Search Bar */
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  align-items: center;
}

.search-input {
  width: 180px;
}

.search-select {
  width: 180px;
}

.search-select.short {
  width: 130px;
}

.search-btn,
.reset-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  border-radius: var(--radius-md);
  font-weight: 500;
}

/* Table */
.data-table {
  border-radius: var(--radius-md);
  overflow: hidden;
}

.data-table :deep(.el-table__row) {
  transition: background-color var(--transition-fast);
}

.cell-primary {
  font-weight: 600;
  color: var(--color-foreground);
}

.cell-mono {
  font-family: var(--font-mono);
  font-size: 13px;
  color: var(--color-text-secondary);
}

.gender-tag {
  min-width: 32px;
  text-align: center;
}

.op-btn {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  cursor: pointer;
  transition: opacity var(--transition-fast);
}

.op-btn:hover {
  opacity: 0.8;
}

/* Pagination */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

/* Dialog */
.dialog-form {
  padding: 4px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn {
  border-radius: var(--radius-md);
}

/* Reduced Motion */
@media (prefers-reduced-motion: reduce) {
  .main-card,
  .action-btn,
  .op-btn,
  .data-table :deep(.el-table__row) {
    transition: none;
  }
  .action-btn:hover {
    transform: none;
  }
}
</style>
