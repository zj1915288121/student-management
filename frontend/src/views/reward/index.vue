<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">
              <SvgIcon name="medal" :size="20" :stroke-width="2" style="color: var(--color-primary)" />
            </span>
            <span>奖惩管理</span>
          </div>
          <div v-if="!isStudent" class="header-btns">
            <el-button type="warning" class="header-action-btn" @click="handleAdd(1)">
              <SvgIcon name="medal" :size="14" :stroke-width="2" />
              新增奖励
            </el-button>
            <el-button type="danger" class="header-action-btn" @click="handleAdd(2)">
              <SvgIcon name="warning" :size="14" :stroke-width="2" />
              新增处分
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="!isStudent" class="search-bar">
        <el-input v-model="query.keyword" placeholder="搜索学号/姓名" clearable class="search-input" @keyup.enter="loadData">
          <template #prefix>
            <SvgIcon name="search" :size="14" :stroke-width="2" style="color: var(--color-text-muted)" />
          </template>
        </el-input>
        <el-select v-model="query.type" placeholder="类型" clearable class="search-select" @change="loadData">
          <el-option label="奖励" :value="1" /><el-option label="处分" :value="2" />
        </el-select>
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" class="search-daterange" @change="handleDateChange" />
        <el-button type="primary" @click="loadData">
          <SvgIcon name="search" :size="14" :stroke-width="2" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <SvgIcon name="reset" :size="14" :stroke-width="2" />
          重置
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe class="data-table">
        <template #empty><el-empty description="暂无奖惩记录" /></template>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column v-if="!isStudent" prop="className" label="班级" width="140" />
        <el-table-column label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'warning' : 'danger'" size="small" class="type-tag">{{ row.type === 1 ? '奖励' : '处分' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="100" />
        <el-table-column prop="content" label="内容" min-width="180" show-overflow-tooltip />
        <el-table-column prop="reason" label="原因" min-width="160" show-overflow-tooltip />
        <el-table-column prop="recordDate" label="记录日期" width="120" />
        <el-table-column v-if="!isStudent" label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" class="action-link" @click="handleEdit(row)">
              <SvgIcon name="edit" :size="13" :stroke-width="2" />
              编辑
            </el-button>
            <el-button type="danger" link size="small" class="action-link" @click="handleDelete(row)">
              <SvgIcon name="delete" :size="13" :stroke-width="2" />
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!isStudent" class="pagination-wrap">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total"
          :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" destroy-on-close class="custom-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" filterable remote :remote-method="searchStudent" :loading="studentSearchLoading" placeholder="搜索学号/姓名" style="width:100%">
            <el-option v-for="s in studentOptions" :key="s.id" :label="`${s.studentNo} - ${s.name}`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">奖励</el-radio><el-radio :value="2">处分</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="级别"><el-input v-model="form.level" placeholder="如：校级/院级/一等等" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" placeholder="请输入奖惩内容" /></el-form-item>
        <el-form-item label="原因"><el-input v-model="form.reason" type="textarea" :rows="2" placeholder="请输入原因" /></el-form-item>
        <el-form-item label="记录日期"><el-date-picker v-model="form.recordDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPageList, add, update, remove, getMyRecords } from '@/api/rewardPunish'
import { searchStudents } from '@/api/student'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const isStudent = userStore.role === 3

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增奖励')
const editId = ref(null)
const tableData = ref([])
const total = ref(0)
const dateRange = ref(null)
const formRef = ref(null)
const studentOptions = ref([])
const studentSearchLoading = ref(false)

const searchStudent = async (query) => {
  if (!query) { studentOptions.value = []; return }
  studentSearchLoading.value = true
  try { const res = await searchStudents(query); studentOptions.value = res.data } catch {}
  finally { studentSearchLoading.value = false }
}

const query = reactive({ keyword: '', type: null, dateStart: '', dateEnd: '', page: 1, size: 10 })
const form = reactive({ studentId: null, type: 1, level: '', content: '', reason: '', recordDate: '' })
const rules = {
  studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const handleDateChange = (val) => {
  query.dateStart = val ? val[0] : ''
  query.dateEnd = val ? val[1] : ''
  loadData()
}

const loadData = async () => {
  loading.value = true
  try {
    if (isStudent) {
      const res = await getMyRecords()
      tableData.value = res.data || []
      total.value = res.data ? res.data.length : 0
    } else {
      const res = await getPageList(query)
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally { loading.value = false }
}

const resetQuery = () => { query.keyword = ''; query.type = null; query.dateStart = ''; query.dateEnd = ''; dateRange.value = null; query.page = 1; loadData() }

const handleAdd = (type) => {
  dialogTitle.value = type === 1 ? '新增奖励' : '新增处分'
  editId.value = null
  Object.assign(form, { studentId: null, type, level: '', content: '', reason: '', recordDate: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = row.type === 1 ? '编辑奖励' : '编辑处分'
  editId.value = row.id
  Object.assign(form, { studentId: row.studentId, type: row.type, level: row.level, content: row.content, reason: row.reason, recordDate: row.recordDate })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (editId.value) { await update(editId.value, form); ElMessage.success('修改成功') }
    else { await add(form); ElMessage.success('新增成功') }
    dialogVisible.value = false; loadData()
  } finally { submitLoading.value = false }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
    .then(async () => { await remove(row.id); ElMessage.success('删除成功'); loadData() }).catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.search-input { width: 180px; }
.search-select { width: 120px; }
.search-daterange { width: 280px; }

.data-table { width: 100%; }

.type-tag { font-weight: 500; }

.action-link {
  cursor: pointer;
  transition: opacity var(--transition-fast);
}
.action-link:hover { opacity: 0.8; }
.action-link svg { vertical-align: -1px; margin-right: 2px; }

.header-action-btn svg { vertical-align: -1px; margin-right: 4px; }

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

@media (prefers-reduced-motion: reduce) {
  .action-link { transition: none; }
}
</style>
