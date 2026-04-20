<template>
  <div class="page-container">
    <el-card class="status-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">
              <SvgIcon name="document" :size="20" :stroke-width="2" style="color: var(--color-primary)" />
            </span>
            <span class="title-text">学籍异动管理</span>
          </div>
          <el-button v-if="isStudent" type="primary" class="action-btn" @click="handleAdd">
            <SvgIcon name="plus" :size="14" :stroke-width="2.5" />
            <span>新增申请</span>
          </el-button>
        </div>
      </template>

      <!-- Search Bar (Admin/Teacher only) -->
      <div v-if="!isStudent" class="search-bar">
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
        <el-select v-model="query.changeType" placeholder="异动类型" clearable class="search-select" @change="loadData">
          <el-option label="转专业" :value="1" />
          <el-option label="休学" :value="2" />
          <el-option label="复学" :value="3" />
          <el-option label="退学" :value="4" />
        </el-select>
        <el-select v-model="query.status" placeholder="审批状态" clearable class="search-select" @change="loadData">
          <el-option label="待审批" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已拒绝" :value="2" />
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
      <el-table :data="tableData" v-loading="loading" stripe class="data-table">
        <template #empty>
          <el-empty description="暂无异动记录">
            <template #image>
              <SvgIcon name="document" :size="120" :stroke-width="0.8" style="color: var(--color-text-muted)" />
            </template>
          </el-empty>
        </template>
        <el-table-column prop="studentNo" label="学号" width="120">
          <template #default="{ row }">
            <span class="cell-mono">{{ row.studentNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="studentName" label="学生姓名" width="100">
          <template #default="{ row }">
            <span class="cell-primary">{{ row.studentName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="异动类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="changeTypeTag(row.changeType)" size="small" effect="plain" round>{{ changeTypeText(row.changeType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请原因" min-width="180" show-overflow-tooltip />
        <el-table-column prop="fromInfo" label="原信息" width="140" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-secondary">{{ row.fromInfo || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="toInfo" label="目标信息" width="140" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-secondary">{{ row.toInfo || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small" effect="plain" round>{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="170">
          <template #default="{ row }">
            <span class="cell-mono">{{ row.applyTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <template v-if="!isStudent">
              <el-button v-if="row.status === 0" type="success" link size="small" class="op-btn" @click="handleApprove(row)">
                <SvgIcon name="check" :size="13" :stroke-width="2" />
                通过
              </el-button>
              <el-button v-if="row.status === 0" type="warning" link size="small" class="op-btn" @click="handleReject(row)">
                <SvgIcon name="x" :size="13" :stroke-width="2" />
                拒绝
              </el-button>
              <el-button type="danger" link size="small" class="op-btn" @click="handleDelete(row)">
                <SvgIcon name="delete" :size="13" :stroke-width="2" />
                删除
              </el-button>
            </template>
            <el-tag v-else :type="statusTag(row.status)" size="small" effect="plain" round>{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination (Admin/Teacher only) -->
      <div v-if="!isStudent" class="pagination-wrap">
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
    <el-dialog v-model="dialogVisible" title="新增异动申请" width="500px" destroy-on-close class="form-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-form-item label="学生" prop="studentId">
          <el-select
            v-model="form.studentId"
            filterable
            remote
            :remote-method="searchStudent"
            :loading="studentSearchLoading"
            placeholder="搜索学号/姓名"
            style="width: 100%"
          >
            <el-option v-for="s in studentOptions" :key="s.id" :label="`${s.studentNo} - ${s.name}`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="异动类型" prop="changeType">
          <el-radio-group v-model="form.changeType">
            <el-radio :value="1">转专业</el-radio>
            <el-radio :value="2">休学</el-radio>
            <el-radio :value="3">复学</el-radio>
            <el-radio :value="4">退学</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="原信息">
          <el-input v-model="form.fromInfo" placeholder="如：原班级|原专业" />
        </el-form-item>
        <el-form-item label="目标信息">
          <el-input v-model="form.toInfo" placeholder="如：新班级ID|新专业" />
        </el-form-item>
        <el-form-item label="申请原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请输入申请原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPageList, apply, approve, reject, remove, getMyList } from '@/api/statusChange'
import { searchStudents } from '@/api/student'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const isStudent = userStore.role === 3

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const total = ref(0)
const formRef = ref(null)
const studentOptions = ref([])
const studentSearchLoading = ref(false)

const searchStudent = async (query) => {
  if (!query) { studentOptions.value = []; return }
  studentSearchLoading.value = true
  try { const res = await searchStudents(query); studentOptions.value = res.data } catch {}
  finally { studentSearchLoading.value = false }
}

const query = reactive({ keyword: '', changeType: null, status: null, page: 1, size: 10 })
const form = reactive({ studentId: null, changeType: 1, reason: '', fromInfo: '', toInfo: '' })
const rules = {
  studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
  changeType: [{ required: true, message: '请选择异动类型', trigger: 'change' }],
  reason: [{ required: true, message: '请输入申请原因', trigger: 'blur' }]
}

const changeTypeTag = (t) => ({ 1: '', 2: 'warning', 3: 'success', 4: 'danger' }[t] || 'info')
const changeTypeText = (t) => ({ 1: '转专业', 2: '休学', 3: '复学', 4: '退学' }[t] || '未知')
const statusTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')
const statusText = (s) => ({ 0: '待审批', 1: '已通过', 2: '已拒绝' }[s] || '未知')

const loadData = async () => {
  loading.value = true
  try {
    if (isStudent) {
      const res = await getMyList(query)
      tableData.value = res.data.records || res.data
      total.value = res.data.total || (res.data && res.data.length) || 0
    } else {
      const res = await getPageList(query)
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.keyword = ''
  query.changeType = null
  query.status = null
  query.page = 1
  loadData()
}

const handleAdd = () => {
  Object.assign(form, { studentId: null, changeType: 1, reason: '', fromInfo: '', toInfo: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await apply(form)
    ElMessage.success('申请提交成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleApprove = (row) => {
  ElMessageBox.confirm(`确定通过"${row.studentName}"的异动申请吗？`, '审批确认', { type: 'success' })
    .then(async () => { await approve(row.id); ElMessage.success('审批通过'); loadData() })
    .catch(() => {})
}

const handleReject = (row) => {
  ElMessageBox.prompt('请输入拒绝原因', '审批拒绝', { confirmButtonText: '确定', cancelButtonText: '取消', inputPlaceholder: '拒绝原因' })
    .then(async ({ value }) => { await reject(row.id, value); ElMessage.success('已拒绝'); loadData() })
    .catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
    .then(async () => { await remove(row.id); ElMessage.success('删除成功'); loadData() })
    .catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.status-card {
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition-normal);
}

.status-card:hover {
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

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
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
  width: 150px;
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

.cell-secondary {
  color: var(--color-text-secondary);
  font-size: 13px;
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
  .status-card,
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
