<template>
  <div class="dashboard-container">
    <!-- Welcome Banner -->
    <section class="welcome-section">
      <div class="welcome-text">
        <h2 class="welcome-greeting">{{ greetingText }}，{{ userStore.userInfo?.realName || '用户' }}</h2>
        <p class="welcome-date">{{ currentDate }}</p>
      </div>
      <div class="welcome-badge">
        <span class="role-badge" :class="'role-badge--' + userStore.role">{{ userStore.roleName }}</span>
      </div>
    </section>

    <!-- Stat Cards -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">学生总数</p>
              <p class="stat-value">{{ stats.studentCount || 0 }}</p>
            </div>
            <div class="stat-icon stat-icon--blue">
              <SvgIcon name="user" :size="22" :stroke-width="2" />
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">班级总数</p>
              <p class="stat-value">{{ stats.classCount || 0 }}</p>
            </div>
            <div class="stat-icon stat-icon--green">
              <SvgIcon name="home" :size="22" :stroke-width="2" />
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">课程总数</p>
              <p class="stat-value">{{ stats.courseCount || 0 }}</p>
            </div>
            <div class="stat-icon stat-icon--yellow">
              <SvgIcon name="book" :size="22" :stroke-width="2" />
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">教师总数</p>
              <p class="stat-value">{{ stats.teacherCount || 0 }}</p>
            </div>
            <div class="stat-icon stat-icon--purple">
              <SvgIcon name="users" :size="22" :stroke-width="2" />
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Charts Row 1 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-group">
              <h3 class="chart-title">成绩分布</h3>
              <span class="chart-tag">本学期</span>
            </div>
          </div>
          <div ref="scoreChartRef" class="chart-body"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-group">
              <h3 class="chart-title">学生状态分布</h3>
              <span class="chart-tag">全部</span>
            </div>
          </div>
          <div ref="statusChartRef" class="chart-body"></div>
        </div>
      </el-col>
    </el-row>

    <!-- Charts Row 2 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-group">
              <h3 class="chart-title">班级平均分排名 Top10</h3>
              <span class="chart-tag">按平均分降序</span>
            </div>
          </div>
          <div ref="classChartRef" class="chart-body"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useUserStore } from '@/store/user'
import { getStats, getScoreDistribution, getStudentStatus, getClassRanking } from '@/api/dashboard'
import * as echarts from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([BarChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

const userStore = useUserStore()
const stats = ref({})
const scoreChartRef = ref(null)
const statusChartRef = ref(null)
const classChartRef = ref(null)
let scoreChart = null
let statusChart = null
let classChart = null

const greetingText = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const currentDate = computed(() => {
  const now = new Date()
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 星期${weekDays[now.getDay()]}`
})

const loadStats = async () => {
  try {
    const res = await getStats()
    stats.value = res.data
  } catch {}
}

const initCharts = async () => {
  await nextTick()
  try {
    const [scoreRes, statusRes, classRes] = await Promise.all([
      getScoreDistribution(),
      getStudentStatus(),
      getClassRanking()
    ])

    // Dark tooltip config
    const darkTooltip = {
      backgroundColor: '#1e293b',
      borderColor: '#334155',
      borderWidth: 1,
      textStyle: { color: '#f1f5f9', fontSize: 13 },
      padding: [8, 12]
    }

    // Score Distribution Bar Chart
    if (scoreChartRef.value) {
      scoreChart = echarts.init(scoreChartRef.value)
      scoreChart.setOption({
        tooltip: { ...darkTooltip, trigger: 'axis', axisPointer: { type: 'shadow', shadowStyle: { color: 'rgba(37,99,235,0.06)' } } },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '8%', containLabel: true },
        xAxis: {
          type: 'category',
          data: scoreRes.data.map(i => i.range),
          axisLine: { lineStyle: { color: '#e2e8f0' } },
          axisTick: { show: false },
          axisLabel: { color: '#64748b', fontSize: 12 }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisTick: { show: false },
          splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } },
          axisLabel: { color: '#64748b', fontSize: 12 }
        },
        series: [{
          type: 'bar',
          data: scoreRes.data.map(i => i.count),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2563eb' },
              { offset: 1, color: '#60a5fa' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          barWidth: '50%',
          emphasis: {
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#1d4ed8' },
                { offset: 1, color: '#3b82f6' }
              ])
            }
          }
        }]
      })
    }

    // Student Status Pie Chart
    if (statusChartRef.value) {
      statusChart = echarts.init(statusChartRef.value)
      const colorPalette = ['#2563eb', '#059669', '#d97706', '#7c3aed', '#dc2626']
      statusChart.setOption({
        tooltip: { ...darkTooltip, trigger: 'item' },
        legend: {
          bottom: 0,
          textStyle: { color: '#64748b', fontSize: 12 },
          itemWidth: 10,
          itemHeight: 10,
          itemGap: 16
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '45%'],
          data: statusRes.data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0,0,0,0.2)'
            }
          },
          label: {
            formatter: '{b}: {c} ({d}%)',
            color: '#334155',
            fontSize: 12
          },
          itemStyle: {
            borderRadius: 6,
            borderColor: '#fff',
            borderWidth: 2
          },
          color: colorPalette
        }]
      })
    }

    // Class Ranking Horizontal Bar Chart
    if (classChartRef.value) {
      classChart = echarts.init(classChartRef.value)
      const classNames = classRes.data.map(i => i.className)
      const avgScores = classRes.data.map(i => i.avgScore)
      classChart.setOption({
        tooltip: { ...darkTooltip, trigger: 'axis', axisPointer: { type: 'shadow', shadowStyle: { color: 'rgba(5,150,105,0.06)' } } },
        grid: { left: '3%', right: '6%', bottom: '3%', top: '8%', containLabel: true },
        xAxis: {
          type: 'value',
          max: 100,
          axisLine: { lineStyle: { color: '#e2e8f0' } },
          axisLabel: { color: '#64748b', fontSize: 12 },
          splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } }
        },
        yAxis: {
          type: 'category',
          data: classNames.reverse(),
          axisLine: { lineStyle: { color: '#e2e8f0' } },
          axisTick: { show: false },
          axisLabel: { color: '#64748b', fontSize: 12, width: 80, overflow: 'truncate' }
        },
        series: [{
          type: 'bar',
          data: avgScores.reverse(),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#059669' },
              { offset: 1, color: '#10b981' }
            ]),
            borderRadius: [0, 4, 4, 0]
          },
          barWidth: '50%',
          label: {
            show: true,
            position: 'right',
            formatter: '{c}',
            color: '#334155',
            fontWeight: 600,
            fontSize: 12
          },
          emphasis: {
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#047857' },
                { offset: 1, color: '#059669' }
              ])
            }
          }
        }]
      })
    }
  } catch {}
}

const handleResize = () => {
  scoreChart?.resize()
  statusChart?.resize()
  classChart?.resize()
}

onMounted(() => {
  loadStats()
  initCharts()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  scoreChart?.dispose()
  statusChart?.dispose()
  classChart?.dispose()
})
</script>

<style scoped>
/* ===== Design Tokens ===== */
.dashboard-container {
  --c-primary: #2563eb;
  --c-primary-light: #3b82f6;
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
  --t-fast: 150ms;
  --t-normal: 200ms;
  --t-slow: 300ms;
}

.dashboard-container {
  padding: 0;
}

/* ===== Welcome Banner ===== */
.welcome-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 24px 28px;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border-radius: var(--r-lg);
  color: #f8fafc;
  position: relative;
  overflow: hidden;
}

.welcome-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(37, 99, 235, 0.08);
  pointer-events: none;
}

.welcome-section::after {
  content: '';
  position: absolute;
  bottom: -30%;
  right: 10%;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(37, 99, 235, 0.05);
  pointer-events: none;
}

.welcome-greeting {
  font-size: 22px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 6px;
  position: relative;
  z-index: 1;
}

.welcome-date {
  font-size: 14px;
  color: #94a3b8;
  position: relative;
  z-index: 1;
}

.welcome-badge {
  position: relative;
  z-index: 1;
}

.role-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 16px;
  border-radius: var(--r-xl);
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.role-badge--1 {
  background: rgba(239, 68, 68, 0.15);
  color: #fca5a5;
  border: 1px solid rgba(239, 68, 68, 0.2);
}

.role-badge--2 {
  background: rgba(245, 158, 11, 0.15);
  color: #fcd34d;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.role-badge--3 {
  background: rgba(37, 99, 235, 0.15);
  color: #93c5fd;
  border: 1px solid rgba(37, 99, 235, 0.2);
}

/* ===== Stat Cards ===== */
.stat-cards {
  margin-top: 0;
}

.stat-card {
  background: #ffffff;
  border-radius: var(--r-lg);
  padding: 20px;
  box-shadow: var(--shadow-xs);
  border: 1px solid #f1f5f9;
  transition: all var(--t-normal) ease;
  cursor: default;
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  margin: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--c-fg);
  margin: 0;
  line-height: 1.2;
  letter-spacing: -0.5px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--r-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon--blue {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.1) 0%, rgba(59, 130, 246, 0.15) 100%);
  color: #2563eb;
}

.stat-icon--green {
  background: linear-gradient(135deg, rgba(5, 150, 105, 0.1) 0%, rgba(16, 185, 129, 0.15) 100%);
  color: #059669;
}

.stat-icon--yellow {
  background: linear-gradient(135deg, rgba(217, 119, 6, 0.1) 0%, rgba(245, 158, 11, 0.15) 100%);
  color: #d97706;
}

.stat-icon--purple {
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.1) 0%, rgba(139, 92, 246, 0.15) 100%);
  color: #7c3aed;
}

/* ===== Chart Cards ===== */
.chart-row {
  margin-top: 16px;
}

.chart-card {
  background: #ffffff;
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-xs);
  border: 1px solid #f1f5f9;
  overflow: hidden;
  transition: box-shadow var(--t-normal) ease;
}

.chart-card:hover {
  box-shadow: var(--shadow-sm);
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px 0;
}

.chart-title-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-title {
  font-weight: 600;
  font-size: 15px;
  color: var(--c-fg);
  margin: 0;
}

.chart-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: var(--r-sm);
  font-size: 11px;
  font-weight: 500;
  color: #64748b;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
}

.chart-body {
  height: 300px;
  padding: 8px 12px 12px;
}

/* ===== Reduced Motion ===== */
@media (prefers-reduced-motion: reduce) {
  .stat-card {
    transition: none;
  }

  .chart-card {
    transition: none;
  }
}
</style>
