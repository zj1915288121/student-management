<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo-container">
        <div class="logo-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/>
          </svg>
        </div>
        <transition name="fade">
          <span v-show="!isCollapse" class="logo-text">学生管理系统</span>
        </transition>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="transparent"
        text-color="rgba(255,255,255,0.6)"
        active-text-color="#ffffff"
        router
        class="sidebar-menu"
      >
        <el-menu-item
          v-for="item in menuList"
          :key="item.path"
          :index="item.path"
          class="menu-item"
        >
          <SvgIcon :name="item.icon" :size="20" :stroke-width="2" class="menu-svg-icon" />
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer" v-show="!isCollapse">
        <div class="sidebar-version">v1.0.0</div>
      </div>
    </el-aside>

    <el-container class="main-container">
      <el-header class="layout-header">
        <div class="header-left">
          <button
            class="collapse-btn"
            @click="isCollapse = !isCollapse"
            :aria-label="isCollapse ? '展开侧边栏' : '收起侧边栏'"
          >
            <svg v-if="isCollapse" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2"/><path d="M15 3v18"/><path d="m10 9-3 3 3 3"/>
            </svg>
            <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2"/><path d="M9 3v18"/><path d="m14 9 3 3-3 3"/>
            </svg>
          </button>
          <div class="breadcrumb">
            <SvgIcon name="home" :size="14" :stroke-width="2" />
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-text">{{ currentTitle }}</span>
          </div>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info" tabindex="0">
              <div class="user-avatar">
                {{ (userStore.userInfo?.realName || userStore.userInfo?.username || 'U').charAt(0) }}
              </div>
              <div class="user-detail">
                <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username || '用户' }}</span>
                <span class="role-tag">{{ userStore.roleName }}</span>
              </div>
              <svg class="arrow-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="6 9 12 15 18 9"/>
              </svg>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <SvgIcon name="user" :size="14" :stroke-width="2" style="margin-right:6px;vertical-align:-2px" />
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:6px;vertical-align:-2px"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const allMenus = [
  { path: '/dashboard', title: '首页', icon: 'home', roles: [1, 2, 3] },
  { path: '/class', title: '班级管理', icon: 'home', roles: [1, 2] },
  { path: '/student', title: '学生管理', icon: 'user', roles: [1, 2] },
  { path: '/status', title: '学籍管理', icon: 'document', roles: [1, 2, 3] },
  { path: '/score', title: '成绩管理', icon: 'barChart', roles: [1, 2, 3] },
  { path: '/course', title: '课程管理', icon: 'book', roles: [1, 2] },
  { path: '/reward', title: '奖惩管理', icon: 'medal', roles: [1, 2, 3] },
  { path: '/scholarship', title: '奖学金管理', icon: 'money', roles: [1, 2, 3] },
  { path: '/system', title: '系统管理', icon: 'settings', roles: [1] }
]

const menuList = computed(() => {
  const role = userStore.role
  if (role === 1) return allMenus
  return allMenus.filter(item => item.roles.includes(role))
})

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  const menu = allMenus.find(item => item.path === route.path)
  return menu ? menu.title : '首页'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logoutAction()
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
/* ===== Design Tokens ===== */
.layout-container {
  --c-primary: #2563eb;
  --c-primary-light: #3b82f6;
  --c-accent: #059669;
  --c-bg: #f8fafc;
  --c-fg: #0f172a;
  --c-sidebar-start: #1e293b;
  --c-sidebar-end: #0f172a;
  --r-sm: 6px;
  --r-md: 8px;
  --r-lg: 12px;
  --r-xl: 16px;
  --shadow-xs: 0 1px 2px rgba(0,0,0,0.05);
  --shadow-sm: 0 1px 3px rgba(0,0,0,0.1), 0 1px 2px rgba(0,0,0,0.06);
  --shadow-md: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06);
  --shadow-lg: 0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -2px rgba(0,0,0,0.05);
  --t-fast: 150ms;
  --t-normal: 200ms;
  --t-slow: 300ms;
}

.layout-container {
  height: 100vh;
  font-family: 'Fira Sans', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* ===== Sidebar ===== */
.layout-aside {
  background: linear-gradient(180deg, var(--c-sidebar-start) 0%, var(--c-sidebar-end) 100%);
  transition: width var(--t-normal) ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 0 16px;
  overflow: hidden;
  white-space: nowrap;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
}

.logo-icon {
  color: #60a5fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-text {
  color: #f1f5f9;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

/* ===== Menu ===== */
.sidebar-menu {
  border-right: none;
  padding: 8px;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar-menu::-webkit-scrollbar {
  width: 4px;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
  margin-bottom: 2px;
  border-radius: var(--r-md);
  transition: all var(--t-normal) ease;
  font-size: 14px;
  padding: 0 16px !important;
}

.sidebar-menu :deep(.el-menu-item .menu-svg-icon) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  flex-shrink: 0;
  margin-right: 0;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.08) !important;
  color: #ffffff !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, var(--c-primary) 0%, var(--c-primary-light) 100%) !important;
  color: #ffffff !important;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.35);
}

.sidebar-menu :deep(.el-menu-item.is-active .menu-svg-icon) {
  color: #ffffff;
}

/* Collapse mode */
.sidebar-menu :deep(.el-menu--collapse .el-menu-item) {
  padding: 0 !important;
  justify-content: center;
}

.sidebar-menu :deep(.el-menu--collapse .el-menu-item .menu-svg-icon) {
  margin-right: 0;
}

/* ===== Sidebar Footer ===== */
.sidebar-footer {
  padding: 12px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
}

.sidebar-version {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.25);
  text-align: center;
  letter-spacing: 0.5px;
}

/* ===== Main Container ===== */
.main-container {
  background-color: var(--c-bg);
}

/* ===== Header ===== */
.layout-header {
  height: 60px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: var(--shadow-xs);
  z-index: 5;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--r-md);
  cursor: pointer;
  color: #64748b;
  transition: all var(--t-normal) ease;
  border: none;
  background: none;
  outline: none;
}

.collapse-btn:hover {
  background-color: #f1f5f9;
  color: var(--c-primary);
}

.collapse-btn:focus-visible {
  outline: 2px solid var(--c-primary);
  outline-offset: 2px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #94a3b8;
  font-weight: 400;
}

.breadcrumb-sep {
  color: #cbd5e1;
  font-size: 12px;
}

.breadcrumb-text {
  color: var(--c-fg);
  font-weight: 600;
  font-size: 14px;
}

/* ===== User Info ===== */
.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--r-lg);
  transition: all var(--t-normal) ease;
  outline: none;
}

.user-info:hover {
  background-color: #f1f5f9;
}

.user-info:focus-visible {
  outline: 2px solid var(--c-primary);
  outline-offset: 2px;
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: var(--r-md);
  background: linear-gradient(135deg, var(--c-primary) 0%, var(--c-primary-light) 100%);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.3);
}

.user-detail {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.username {
  font-weight: 600;
  font-size: 14px;
  color: var(--c-fg);
}

.role-tag {
  font-size: 11px;
  color: var(--c-primary);
  font-weight: 500;
}

.arrow-icon {
  color: #94a3b8;
  transition: transform var(--t-normal) ease;
}

/* ===== Main Content ===== */
.layout-main {
  padding: 20px;
  overflow-y: auto;
  background-color: var(--c-bg);
}

/* ===== Transitions ===== */
.page-fade-enter-active {
  transition: opacity var(--t-normal) ease;
}

.page-fade-leave-active {
  transition: opacity var(--t-fast) ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

.fade-enter-active {
  transition: opacity var(--t-slow) ease;
}

.fade-leave-active {
  transition: opacity var(--t-fast) ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ===== Focus-Visible ===== */
.sidebar-menu :deep(.el-menu-item:focus-visible) {
  outline: 2px solid var(--c-primary);
  outline-offset: -2px;
}

/* ===== Reduced Motion ===== */
@media (prefers-reduced-motion: reduce) {
  .layout-aside {
    transition: none;
  }

  .sidebar-menu :deep(.el-menu-item) {
    transition: none;
  }

  .collapse-btn,
  .user-info,
  .user-avatar {
    transition: none;
  }

  .page-fade-enter-active,
  .page-fade-leave-active,
  .fade-enter-active,
  .fade-leave-active {
    transition: none;
  }
}
</style>
