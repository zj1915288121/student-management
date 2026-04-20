<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">
              <SvgIcon name="course" :size="20" :stroke-width="2" style="color: var(--color-primary)" />
            </span>
            <span>课程管理</span>
          </div>
          <el-button v-if="isAdmin" type="primary" class="header-action-btn" @click="handleAdd">
            <SvgIcon name="plus" :size="14" :stroke-width="2.5" />
            新增课程
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="搜索课程名称/代码" clearable class="search-input" @keyup.enter="loadData">
          <template #prefix>
            <SvgIcon name="search" :size="14" :stroke-width="2" style="color: var(--color-text-muted)" />
          </template>
        </el-input>
        <el-input v-model="query.semester" placeholder="学期" clearable class="search-input-sm" @keyup.enter="loadData" />
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
        <template #empty><el-empty description="暂无课程数据" /></template>
        <el-table-column prop="courseName" label="课程名称" min-width="140" />
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column prop="classHours" label="学时" width="80" align="center" />
        <el-table-column prop="teacherName" label="授课教师" width="100" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="description" label="课程描述" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <template v-if="isAdmin">
              <el-button type="primary" link size="small" class="action-link" @click="handleEdit(row)">
                <SvgIcon name="edit" :size="13" :stroke-width="2" />
                编辑
              </el-button>
              <el-button type="danger" link size="small" class="action-link" @click="handleDelete(row)">
                <SvgIcon name="delete" :size="13" :stroke-width="2" />
                删除
              </el-button>
            </template>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total"
          :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close class="custom-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName"><el-input v-model="form.courseName" placeholder="请输入课程名称" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程代码"><el-input v-model="form.courseCode" placeholder="请输入课程代码" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学分" prop="credit"><el-input-number v-model="form.credit" :min="0.5" :max="10" :precision="1" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学时"><el-input-number v-model="form.classHours" :min="1" :max="200" style="width:100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="授课教师"><el-input v-model="form.teacherName" placeholder="请输入教师姓名" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学期"><el-input v-model="form.semester" placeholder="如：2025-2026-1" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="课程描述"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入课程描述" /></el-form-item>
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
import { getPageList, add, update, remove } from '@/api/course'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const isAdmin = userStore.role === 1

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增课程')
const editId = ref(null)
const tableData = ref([])
const total = ref(0)
const formRef = ref(null)

const query = reactive({ keyword: '', semester: '', page: 1, size: 10 })
const form = reactive({ courseName: '', courseCode: '', credit: 1, classHours: 32, teacherName: '', semester: '', description: '' })
const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try { const res = await getPageList(query); tableData.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

const resetQuery = () => { query.keyword = ''; query.semester = ''; query.page = 1; loadData() }

const handleAdd = () => {
  dialogTitle.value = '新增课程'; editId.value = null
  Object.assign(form, { courseName: '', courseCode: '', credit: 1, classHours: 32, teacherName: '', semester: '', description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑课程'; editId.value = row.id
  Object.assign(form, { courseName: row.courseName, courseCode: row.courseCode, credit: row.credit, classHours: row.classHours, teacherName: row.teacherName, semester: row.semester, description: row.description })
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
  ElMessageBox.confirm(`确定删除课程"${row.courseName}"吗？`, '提示', { type: 'warning' })
    .then(async () => { await remove(row.id); ElMessage.success('删除成功'); loadData() }).catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.search-input { width: 220px; }
.search-input-sm { width: 150px; }

.data-table { width: 100%; }

.action-link {
  cursor: pointer;
  transition: opacity var(--transition-fast);
}
.action-link:hover { opacity: 0.8; }

.action-link svg { vertical-align: -1px; margin-right: 2px; }

.text-muted { color: var(--color-text-muted); }

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
