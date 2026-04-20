<template>
  <div class="page-container">
    <!-- Stats Cards (Admin/Teacher only) -->
    <el-row v-if="!isStudent" :gutter="16" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">平均分</p>
              <p class="stat-value">{{ scoreStats.avgScore || 0 }}</p>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light))">
              <SvgIcon name="barChart" :size="22" :stroke-width="2" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">及格率</p>
              <p class="stat-value">{{ scoreStats.passRate || 0 }}%</p>
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
              <p class="stat-label">最高分</p>
              <p class="stat-value">{{ scoreStats.maxScore || 0 }}</p>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, #d97706, #f59e0b)">
              <SvgIcon name="arrowUp" :size="22" :stroke-width="2" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">最低分</p>
              <p class="stat-value">{{ scoreStats.minScore || 0 }}</p>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, #dc2626, #ef4444)">
              <SvgIcon name="arrowDown" :size="22" :stroke-width="2" />
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
              <SvgIcon name="barChart" :size="20" :stroke-width="2" style="color: var(--color-primary)" />
            </span>
            <span class="title-text">成绩管理</span>
          </div>
          <div v-if="!isStudent" class="header-btns">
            <el-button type="primary" class="action-btn" @click="handleBatchAdd">
              <SvgIcon name="plus" :size="14" :stroke-width="2.5" />
              <span>批量录入</span>
            </el-button>
            <el-button type="warning" class="action-btn" @click="handleExport">
              <SvgIcon name="download" :size="14" :stroke-width="2" />
              <span>导出</span>
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
        <el-select v-model="query.courseId" placeholder="课程" clearable class="search-select" @change="loadData">
          <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
        </el-select>
        <el-input
          v-model="query.semester"
          placeholder="学期"
          clearable
          class="search-input short"
          @keyup.enter="loadData"
        />
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
          <el-empty description="暂无成绩数据">
            <template #image>
              <SvgIcon name="barChart" :size="120" :stroke-width="0.8" style="color: var(--color-text-muted)" />
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
        <el-table-column prop="courseName" label="课程名称" min-width="140" />
        <el-table-column prop="semester" label="学期" width="120">
          <template #default="{ row }">
            <span class="cell-mono">{{ row.semester }}</span>
          </template>
        </el-table-column>
        <el-table-column label="成绩" width="80" align="center">
          <template #default="{ row }">
            <span :class="row.score >= 60 ? 'score-pass' : 'score-fail'">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column label="是否及格" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.passed ? 'success' : 'danger'" size="small" effect="plain" round>{{ row.passed ? '及格' : '不及格' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="考试类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" round type="info">{{ row.examType === 1 ? '正常' : row.examType === 2 ? '补考' : '重修' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <template v-if="!isStudent">
              <el-button type="primary" link size="small" class="op-btn" @click="handleEdit(row)">
                <SvgIcon name="edit" :size="13" :stroke-width="2" />
                编辑
              </el-button>
              <el-button type="danger" link size="small" class="op-btn" @click="handleDelete(row)">
                <SvgIcon name="delete" :size="13" :stroke-width="2" />
                删除
              </el-button>
            </template>
            <span v-else class="no-op">-</span>
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

    <!-- Batch Add Dialog -->
    <el-dialog v-model="batchDialogVisible" title="批量录入成绩" width="700px" destroy-on-close class="form-dialog">
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="80px" class="dialog-form">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="课程" prop="courseId">
              <el-select v-model="batchForm.courseId" placeholder="选择课程" style="width: 100%">
                <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-input v-model="batchForm.semester" placeholder="如：2025-2026-1" />
            </el-form-item>
          </el-col>
        </el-row>
        <div class="batch-table-wrap">
          <el-table :data="batchForm.scores" class="batch-table">
            <el-table-column prop="studentId" label="学生ID" width="100" />
            <el-table-column label="成绩" width="150">
              <template #default="{ row }">
                <el-input-number v-model="row.score" :min="0" :max="100" :precision="1" size="small" />
              </template>
            </el-table-column>
          </el-table>
        </div>
        <el-button type="primary" link class="add-row-btn" @click="addBatchRow">
          <SvgIcon name="plus" :size="14" :stroke-width="2" />
          添加学生行
        </el-button>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="batchDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleBatchSubmit">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Edit Dialog -->
    <el-dialog v-model="editDialogVisible" title="编辑成绩" width="400px" destroy-on-close class="form-dialog">
      <el-form :model="editForm" label-width="80px" class="dialog-form">
        <el-form-item label="成绩">
          <el-input-number v-model="editForm.score" :min="0" :max="100" :precision="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleEditSubmit">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPageList, add, batchAdd, update, remove, getStats, exportExcel, getMyScores } from '@/api/score'
import { getAll as getAllCourses } from '@/api/course'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const isStudent = userStore.role === 3

const loading = ref(false)
const submitLoading = ref(false)
const batchDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editId = ref(null)
const tableData = ref([])
const total = ref(0)
const courseList = ref([])
const scoreStats = ref({})
const batchFormRef = ref(null)

const query = reactive({ keyword: '', courseId: null, semester: '', page: 1, size: 10 })
const batchForm = reactive({ courseId: null, semester: '', scores: [] })
const editForm = reactive({ score: 0 })
const batchRules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  semester: [{ required: true, message: '请输入学期', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    if (isStudent) {
      const res = await getMyScores()
      tableData.value = res.data
      total.value = res.data.length
    } else {
      const res = await getPageList(query)
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try { const res = await getStats(query); scoreStats.value = res.data } catch {}
}

const loadCourses = async () => {
  try { const res = await getAllCourses(); courseList.value = res.data } catch {}
}

const resetQuery = () => {
  query.keyword = ''
  query.courseId = null
  query.semester = ''
  query.page = 1
  loadData()
  loadStats()
}

const handleBatchAdd = () => {
  batchForm.courseId = null
  batchForm.semester = ''
  batchForm.scores = [{ studentId: null, score: null, examType: 1 }]
  batchDialogVisible.value = true
}

const addBatchRow = () => {
  batchForm.scores.push({ studentId: null, score: null, examType: 1 })
}

const handleBatchSubmit = async () => {
  const v = await batchFormRef.value.validate().catch(() => false)
  if (!v) return
  if (!batchForm.scores.length || batchForm.scores.every(s => !s.studentId)) {
    ElMessage.warning('请至少添加一条成绩记录')
    return
  }
  submitLoading.value = true
  try {
    await batchAdd(batchForm)
    ElMessage.success('批量录入成功')
    batchDialogVisible.value = false
    loadData()
    loadStats()
  } finally {
    submitLoading.value = false
  }
}

const handleEdit = (row) => {
  editId.value = row.id
  editForm.score = row.score
  editDialogVisible.value = true
}

const handleEditSubmit = async () => {
  submitLoading.value = true
  try {
    await update(editId.value, {
      studentId: tableData.value.find(r => r.id === editId.value)?.studentId,
      courseId: tableData.value.find(r => r.id === editId.value)?.courseId,
      score: editForm.score
    })
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该成绩记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await remove(row.id)
      ElMessage.success('删除成功')
      loadData()
      loadStats()
    })
    .catch(() => {})
}

const handleExport = async () => {
  try {
    const res = await exportExcel(query)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'scores.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
  } catch {}
}

onMounted(() => {
  loadData()
  if (!isStudent) { loadStats(); loadCourses() }
})
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

.search-input.short {
  width: 140px;
}

.search-select {
  width: 180px;
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

.score-pass {
  color: var(--color-accent);
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}

.score-fail {
  color: var(--color-destructive);
  font-weight: 700;
  font-variant-numeric: tabular-nums;
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

.no-op {
  color: var(--color-text-muted);
  font-size: 14px;
}

/* Pagination */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

/* Batch Table */
.batch-table-wrap {
  margin-top: 12px;
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--color-border);
}

.batch-table {
  border-radius: 0;
}

.add-row-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-top: 10px;
  font-weight: 500;
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
