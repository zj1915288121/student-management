<template>
  <div class="page-container">
    <el-card class="class-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">
              <SvgIcon name="home" :size="20" :stroke-width="2" style="color: var(--color-primary)" />
            </span>
            <span class="title-text">班级管理</span>
          </div>
          <el-button v-if="isAdmin" type="primary" class="action-btn" @click="handleAdd">
            <SvgIcon name="plus" :size="14" :stroke-width="2.5" />
            <span>新增班级</span>
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="搜索班级名称" clearable class="search-input" @keyup.enter="loadData">
          <template #prefix>
            <SvgIcon name="search" :size="14" :stroke-width="2" style="color: var(--color-text-muted)" />
          </template>
        </el-input>
        <el-input v-model="query.major" placeholder="专业筛选" clearable class="search-input" @keyup.enter="loadData" />
        <el-input v-model="query.grade" placeholder="年级筛选" clearable class="search-input short" @keyup.enter="loadData" />
        <el-button type="primary" class="search-btn" @click="loadData">
          <SvgIcon name="search" :size="14" :stroke-width="2" />
          <span>搜索</span>
        </el-button>
        <el-button class="reset-btn" @click="resetQuery">
          <SvgIcon name="reset" :size="14" :stroke-width="2" />
          <span>重置</span>
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe class="data-table">
        <template #empty>
          <el-empty description="暂无班级数据">
            <template #image>
              <SvgIcon name="home" :size="120" :stroke-width="0.8" style="color: var(--color-text-muted)" />
            </template>
          </el-empty>
        </template>
        <el-table-column prop="className" label="班级名称" min-width="150">
          <template #default="{ row }">
            <span class="cell-primary">{{ row.className }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="major" label="专业" min-width="150" />
        <el-table-column prop="grade" label="年级" width="100" align="center" />
        <el-table-column prop="teacherName" label="班主任" width="120" align="center" />
        <el-table-column prop="studentCount" label="学生人数" width="110" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" round class="count-tag">{{ row.studentCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <template v-if="isAdmin">
              <el-button type="primary" link size="small" class="op-btn" @click="handleEdit(row)">
                <SvgIcon name="edit" :size="13" :stroke-width="2" />编辑
              </el-button>
              <el-button type="danger" link size="small" class="op-btn" @click="handleDelete(row)">
                <SvgIcon name="delete" :size="13" :stroke-width="2" />删除
              </el-button>
            </template>
            <span v-else class="no-op">-</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close class="form-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-form-item label="班级名称" prop="className"><el-input v-model="form.className" placeholder="请输入班级名称" /></el-form-item>
        <el-form-item label="专业" prop="major"><el-input v-model="form.major" placeholder="请输入专业" /></el-form-item>
        <el-form-item label="年级" prop="grade"><el-input v-model="form.grade" placeholder="请输入年级" /></el-form-item>
        <el-form-item label="班主任"><el-input v-model="form.teacherName" placeholder="请输入班主任姓名" /></el-form-item>
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
import { getPageList, add, update, remove } from '@/api/classes'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const isAdmin = userStore.role === 1

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增班级')
const editId = ref(null)
const tableData = ref([])
const total = ref(0)
const formRef = ref(null)

const query = reactive({ keyword: '', major: '', grade: '', page: 1, size: 10 })
const form = reactive({ className: '', major: '', grade: '', teacherName: '' })
const rules = {
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  grade: [{ required: true, message: '请输入年级', trigger: 'blur' }]
}

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

const resetQuery = () => {
  query.keyword = ''
  query.major = ''
  query.grade = ''
  query.page = 1
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增班级'
  editId.value = null
  Object.assign(form, { className: '', major: '', grade: '', teacherName: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑班级'
  editId.value = row.id
  Object.assign(form, { className: row.className, major: row.major, grade: row.grade, teacherName: row.teacherName })
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
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除班级"${row.className}"吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await remove(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.class-card { border-radius: var(--radius-xl); border: 1px solid var(--color-border); box-shadow: var(--shadow-sm); transition: box-shadow var(--transition-normal); }
.class-card:hover { box-shadow: var(--shadow-md); }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.header-title { display: flex; align-items: center; gap: 10px; font-weight: 600; }
.title-icon { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: var(--radius-md); background: rgba(37, 99, 235, 0.08); }
.title-text { font-size: 16px; color: var(--color-foreground); letter-spacing: 0.3px; }
.action-btn { display: inline-flex; align-items: center; gap: 6px; border-radius: var(--radius-md); font-weight: 500; transition: all var(--transition-fast); }
.action-btn:hover { transform: translateY(-1px); box-shadow: var(--shadow-md); }
.search-bar { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; align-items: center; }
.search-input { width: 200px; }
.search-input.short { width: 140px; }
.search-btn, .reset-btn { display: inline-flex; align-items: center; gap: 5px; border-radius: var(--radius-md); font-weight: 500; }
.data-table { border-radius: var(--radius-md); overflow: hidden; }
.cell-primary { font-weight: 600; color: var(--color-foreground); }
.count-tag { font-variant-numeric: tabular-nums; font-weight: 600; min-width: 28px; text-align: center; }
.op-btn { display: inline-flex; align-items: center; gap: 3px; cursor: pointer; transition: opacity var(--transition-fast); }
.op-btn:hover { opacity: 0.8; }
.no-op { color: var(--color-text-muted); font-size: 14px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 20px; padding-top: 16px; border-top: 1px solid var(--color-border); }
.dialog-form { padding: 4px 0; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }
.cancel-btn { border-radius: var(--radius-md); }
@media (prefers-reduced-motion: reduce) {
  .class-card, .action-btn, .op-btn { transition: none; }
  .action-btn:hover { transform: none; }
}
</style>
