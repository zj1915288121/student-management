import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: 'class',
        name: 'Class',
        component: () => import('@/views/class/index.vue'),
        meta: { title: '班级管理', icon: 'School', roles: [1, 2] }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('@/views/student/index.vue'),
        meta: { title: '学生管理', icon: 'User', roles: [1, 2] }
      },
      {
        path: 'status',
        name: 'Status',
        component: () => import('@/views/status/index.vue'),
        meta: { title: '学籍管理', icon: 'Document', roles: [1, 2, 3] }
      },
      {
        path: 'score',
        name: 'Score',
        component: () => import('@/views/score/index.vue'),
        meta: { title: '成绩管理', icon: 'DataLine', roles: [1, 2, 3] }
      },
      {
        path: 'course',
        name: 'Course',
        component: () => import('@/views/course/index.vue'),
        meta: { title: '课程管理', icon: 'Reading', roles: [1, 2] }
      },
      {
        path: 'reward',
        name: 'Reward',
        component: () => import('@/views/reward/index.vue'),
        meta: { title: '奖惩管理', icon: 'Medal', roles: [1, 2, 3] }
      },
      {
        path: 'scholarship',
        name: 'Scholarship',
        component: () => import('@/views/scholarship/index.vue'),
        meta: { title: '奖学金管理', icon: 'Money', roles: [1, 2, 3] }
      },
      {
        path: 'system',
        name: 'System',
        component: () => import('@/views/system/index.vue'),
        meta: { title: '系统管理', icon: 'Setting', roles: [1] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'UserFilled', roles: [1, 2, 3] }
      }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/dashboard')
  } else if (to.meta.roles && userStore.isLoggedIn && !to.meta.roles.includes(userStore.role)) {
    ElMessage.warning('您没有权限访问该页面')
    next('/dashboard')
  } else {
    next()
  }
})

export default router
