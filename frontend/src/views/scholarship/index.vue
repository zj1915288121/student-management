<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">
              <SvgIcon name="money" :size="20" :stroke-width="2" style="color: var(--color-primary)" />
            </span>
            <span>奖学金管理</span>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="custom-tabs">
        <!-- Admin: 奖学金类型 -->
        <el-tab-pane v-if="userStore.role === 1" name="type">
          <template #label>
            <span class="tab-label">
              <SvgIcon name="tag" :size="14" :stroke-width="2" />
              奖学金类型
            </span>
          </template>
          <div class="search-bar">
            <el-input v-model="typeQuery.keyword" placeholder="搜索名称" clearable class="search-input" @keyup.enter="loadTypes">
              <template #prefix>
                <SvgIcon name="search" :size="14" :stroke-width="2" style="color: var(--color-text-muted)" />
              </template>
            </el-input>
            <el-select v-model="typeQuery.status" placeholder="状态" clearable class="search-select" @change="loadTypes">
              <el-option label="启用" :value="1" /><el-option label="停用" :value="0" />
            </el-select>
            <el-button type="primary" @click="loadTypes">
              <SvgIcon name="search" :size="14" :stroke-width="2" />
              搜索
            </el-button>
            <el-button @click="resetTypeQuery">
              <SvgIcon name="reset" :size="14" :stroke-width="2" />
              重置
            </el-button>
            <el-button type="primary" @click="handleAddType">
              <SvgIcon name="plus" :size="14" :stroke-width="2.5" />
              新增
            </el-button>
          </div>
          <el-table :data="typeData" v-loading="typeLoading" stripe class="data-table">
            <template #empty><el-empty description="暂无奖学金类型" /></template>
            <el-table-column prop="name" label="名称" min-width="140" />
            <el-table-column prop="level" label="等级" width="100" />
            <el-table-column prop="amount" label="金额(元)" width="120" align="right" />
            <el-table-column prop="description" label="说明" min-width="160" show-overflow-tooltip />
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right" align="center">
              <template #default="{ row }">
                <el-button type="primary" link size="small" class="action-link" @click="handleEditType(row)">
                  <SvgIcon name="edit" :size="13" :stroke-width="2" />
                  编辑
                </el-button>
                <el-button :type="row.status === 1 ? 'warning' : 'success'" link size="small" class="action-link" @click="handleToggle(row)">
                  <SvgIcon v-if="row.status === 1" name="x" :size="13" :stroke-width="2" />
                  <SvgIcon v-else name="check" :size="13" :stroke-width="2" />
                  {{ row.status === 1 ? '停用' : '启用' }}
                </el-button>
                <el-button type="danger" link size="small" class="action-link" @click="handleDeleteType(row)">
                  <SvgIcon name="delete" :size="13" :stroke-width="2" />
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-wrap">
            <el-pagination v-model:current-page="typeQuery.page" v-model:page-size="typeQuery.size" :total="typeTotal"
              :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @size-change="loadTypes" @current-change="loadTypes" />
          </div>
        </el-tab-pane>

        <!-- Admin: 申请列表 -->
        <el-tab-pane v-if="userStore.role === 1" name="apply">
          <template #label>
            <span class="tab-label">
              <SvgIcon name="document" :size="14" :stroke-width="2" />
              申请列表
            </span>
          </template>
          <div class="search-bar">
            <el-input v-model="applyQuery.keyword" placeholder="搜索学号/姓名" clearable class="search-input" @keyup.enter="loadApplies">
              <template #prefix>
                <SvgIcon name="search" :size="14" :stroke-width="2" style="color: var(--color-text-muted)" />
              </template>
            </el-input>
            <el-select v-model="applyQuery.status" placeholder="审批状态" clearable class="search-select" @change="loadApplies">
              <el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已拒绝" :value="2" />
            </el-select>
            <el-button type="primary" @click="loadApplies">
              <SvgIcon name="search" :size="14" :stroke-width="2" />
              搜索
            </el-button>
            <el-button @click="applyQuery.keyword='';applyQuery.status=null;loadApplies">
              <SvgIcon name="reset" :size="14" :stroke-width="2" />
              重置
            </el-button>
          </div>
          <el-table :data="applyData" v-loading="applyLoading" stripe class="data-table">
            <template #empty><el-empty description="暂无申请记录" /></template>
            <el-table-column prop="studentNo" label="学号" width="120" />
            <el-table-column prop="studentName" label="学生姓名" width="100" />
            <el-table-column prop="scholarshipName" label="奖学金名称" min-width="140" />
            <el-table-column prop="amount" label="金额(元)" width="100" align="right" />
            <el-table-column prop="applyReason" label="申请理由" min-width="160" show-overflow-tooltip />
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="applyStatusTag(row.status)" size="small">{{ applyStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applyTime" label="申请时间" width="170" />
            <el-table-column label="操作" width="160" fixed="right" align="center">
              <template #default="{ row }">
                <template v-if="row.status === 0">
                  <el-button type="success" link size="small" class="action-link" @click="handleApprove(row)">
                    <SvgIcon name="check" :size="13" :stroke-width="2" />
                    通过
                  </el-button>
                  <el-button type="warning" link size="small" class="action-link" @click="handleReject(row)">
                    <SvgIcon name="x" :size="13" :stroke-width="2" />
                    拒绝
                  </el-button>
                </template>
                <span v-else class="text-muted">-</span>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-wrap">
            <el-pagination v-model:current-page="applyQuery.page" v-model:page-size="applyQuery.size" :total="applyTotal"
              :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @size-change="loadApplies" @current-change="loadApplies" />
          </div>
        </el-tab-pane>

        <!-- Student: 我的申请 -->
        <el-tab-pane v-if="userStore.role === 3" name="my">
          <template #label>
            <span class="tab-label">
              <SvgIcon name="user" :size="14" :stroke-width="2" />
              我的申请
            </span>
          </template>
          <div class="search-bar">
            <el-button type="primary" @click="handleMyApply">
              <SvgIcon name="plus" :size="14" :stroke-width="2.5" />
              申请奖学金
            </el-button>
          </div>
          <el-table :data="myData" v-loading="myLoading" stripe class="data-table">
            <template #empty><el-empty description="暂无申请记录" /></template>
            <el-table-column prop="scholarshipName" label="奖学金名称" min-width="140" />
            <el-table-column prop="amount" label="金额(元)" width="100" align="right" />
            <el-table-column prop="applyReason" label="申请理由" min-width="160" show-overflow-tooltip />
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="applyStatusTag(row.status)" size="small">{{ applyStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applyTime" label="申请时间" width="170" />
          </el-table>
          <div class="pagination-wrap">
            <el-pagination v-model:current-page="myPage" v-model:page-size="mySize" :total="myTotal"
              layout="total,prev,pager,next" @current-change="loadMyApplies" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 类型编辑弹窗 -->
    <el-dialog v-model="typeDialogVisible" :title="typeDialogTitle" width="520px" destroy-on-close class="custom-dialog">
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="80px" class="dialog-form">
        <el-form-item label="名称" prop="name"><el-input v-model="typeForm.name" placeholder="请输入奖学金名称" /></el-form-item>
        <el-form-item label="等级"><el-input v-model="typeForm.level" placeholder="如：国家级/校级" /></el-form-item>
        <el-form-item label="金额" prop="amount"><el-input-number v-model="typeForm.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="typeForm.description" type="textarea" :rows="2" placeholder="请输入说明" /></el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="typeDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleTypeSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 申请弹窗 -->
    <el-dialog v-model="myApplyDialogVisible" title="申请奖学金" width="520px" destroy-on-close class="custom-dialog">
      <el-form ref="applyFormRef" :model="applyForm" :rules="applyRules" label-width="80px" class="dialog-form">
        <el-form-item label="奖学金" prop="scholarshipId">
          <el-select v-model="applyForm.scholarshipId" placeholder="选择奖学金" style="width:100%">
            <el-option v-for="s in scholarshipList" :key="s.id" :label="`${s.name} (${s.amount}元)`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请理由" prop="applyReason"><el-input v-model="applyForm.applyReason" type="textarea" :rows="3" placeholder="请输入申请理由" /></el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="myApplyDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleApplySubmit">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPageList, add, update, remove, toggleStatus, getAll, getApplyPageList, apply, approve, reject, getMyApplyList } from '@/api/scholarship'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const activeTab = ref('type')
const submitLoading = ref(false)

const typeLoading = ref(false); const typeData = ref([]); const typeTotal = ref(0)
const typeQuery = reactive({ keyword: '', status: null, page: 1, size: 10 })
const typeDialogVisible = ref(false); const typeDialogTitle = ref('新增奖学金'); const editTypeId = ref(null)
const typeForm = reactive({ name: '', level: '', amount: 0, description: '' })
const typeRules = { name: [{ required: true, message: '请输入名称', trigger: 'blur' }], amount: [{ required: true, message: '请输入金额', trigger: 'blur' }] }
const typeFormRef = ref(null)

const applyLoading = ref(false); const applyData = ref([]); const applyTotal = ref(0)
const applyQuery = reactive({ keyword: '', status: null, page: 1, size: 10 })

const myLoading = ref(false); const myData = ref([]); const myTotal = ref(0); const myPage = ref(1); const mySize = ref(10)
const myApplyDialogVisible = ref(false); const scholarshipList = ref([])
const applyForm = reactive({ studentId: null, scholarshipId: null, applyReason: '' })
const applyRules = { scholarshipId: [{ required: true, message: '请选择奖学金', trigger: 'change' }], applyReason: [{ required: true, message: '请输入申请理由', trigger: 'blur' }] }
const applyFormRef = ref(null)

const applyStatusTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')
const applyStatusText = (s) => ({ 0: '待审核', 1: '已通过', 2: '已拒绝' }[s] || '未知')

const loadTypes = async () => { typeLoading.value = true; try { const res = await getPageList(typeQuery); typeData.value = res.data.records; typeTotal.value = res.data.total } finally { typeLoading.value = false } }
const resetTypeQuery = () => { typeQuery.keyword = ''; typeQuery.status = null; typeQuery.page = 1; loadTypes() }
const loadApplies = async () => { applyLoading.value = true; try { const res = await getApplyPageList(applyQuery); applyData.value = res.data.records; applyTotal.value = res.data.total } finally { applyLoading.value = false } }
const loadMyApplies = async () => { myLoading.value = true; try { const res = await getMyApplyList({ page: myPage.value, size: mySize.value }); myData.value = res.data.records; myTotal.value = res.data.total } finally { myLoading.value = false } }

const handleAddType = () => { typeDialogTitle.value = '新增奖学金'; editTypeId.value = null; Object.assign(typeForm, { name: '', level: '', amount: 0, description: '' }); typeDialogVisible.value = true }
const handleEditType = (row) => { typeDialogTitle.value = '编辑奖学金'; editTypeId.value = row.id; Object.assign(typeForm, { name: row.name, level: row.level, amount: row.amount, description: row.description }); typeDialogVisible.value = true }
const handleTypeSubmit = async () => { const v = await typeFormRef.value.validate().catch(() => false); if (!v) return; submitLoading.value = true; try { if (editTypeId.value) { await update(editTypeId.value, typeForm); ElMessage.success('修改成功') } else { await add(typeForm); ElMessage.success('新增成功') } typeDialogVisible.value = false; loadTypes() } finally { submitLoading.value = false } }
const handleToggle = async (row) => { await toggleStatus(row.id); ElMessage.success(row.status === 1 ? '已停用' : '已启用'); loadTypes() }
const handleDeleteType = (row) => { ElMessageBox.confirm(`确定删除"${row.name}"吗？`, '提示', { type: 'warning' }).then(async () => { await remove(row.id); ElMessage.success('删除成功'); loadTypes() }).catch(() => {}) }

const handleApprove = (row) => { ElMessageBox.confirm('确定通过该申请吗？', '审批确认', { type: 'success' }).then(async () => { await approve(row.id); ElMessage.success('审批通过'); loadApplies() }).catch(() => {}) }
const handleReject = (row) => { ElMessageBox.prompt('请输入拒绝原因', '审批拒绝', { confirmButtonText: '确定', cancelButtonText: '取消' }).then(async ({ value }) => { await reject(row.id, value); ElMessage.success('已拒绝'); loadApplies() }).catch(() => {}) }

const handleMyApply = async () => { try { const res = await getAll(); scholarshipList.value = res.data } catch {} applyForm.scholarshipId = null; applyForm.applyReason = ''; myApplyDialogVisible.value = true }
const handleApplySubmit = async () => { const v = await applyFormRef.value.validate().catch(() => false); if (!v) return; applyForm.studentId = userStore.userInfo?.id; submitLoading.value = true; try { await apply(applyForm); ElMessage.success('申请提交成功'); myApplyDialogVisible.value = false; loadMyApplies() } finally { submitLoading.value = false } }

onMounted(() => {
  if (userStore.role === 1) { activeTab.value = 'type'; loadTypes(); loadApplies() }
  else if (userStore.role === 3) { activeTab.value = 'my'; loadMyApplies() }
})
</script>

<style scoped>
.search-input { width: 200px; }
.search-select { width: 130px; }

.data-table { width: 100%; }

.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.tab-label svg { vertical-align: -1px; }

.action-link {
  cursor: pointer;
  transition: opacity var(--transition-fast);
}
.action-link:hover { opacity: 0.8; }
.action-link svg { vertical-align: -1px; margin-right: 2px; }

.text-muted { color: var(--color-text-muted); }

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

@media (prefers-reduced-motion: reduce) {
  .action-link { transition: none; }
  .custom-tabs :deep(.el-tabs__item) { transition: none; }
}
</style>
