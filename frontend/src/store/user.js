import { defineStore } from 'pinia'
import { login as loginApi, getInfo as getInfoApi, logout as logoutApi } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    role: (state) => state.userInfo?.role || 0,
    roleName: (state) => state.userInfo?.roleName || ''
  },

  actions: {
    async loginAction(loginForm) {
      const res = await loginApi(loginForm)
      this.token = res.data.token
      const info = res.data.userInfo
      const safeInfo = {
        id: info.id,
        username: info.username,
        realName: info.realName,
        role: info.role,
        roleName: info.roleName
      }
      this.userInfo = safeInfo
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(safeInfo))
      return res
    },

    async getInfoAction() {
      const res = await getInfoApi()
      const info = res.data
      const safeInfo = {
        id: info.id,
        username: info.username,
        realName: info.realName,
        role: info.role,
        roleName: info.roleName
      }
      this.userInfo = safeInfo
      localStorage.setItem('userInfo', JSON.stringify(safeInfo))
      return res
    },

    async logoutAction() {
      try {
        await logoutApi()
      } catch (e) {
        // ignore
      }
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }
  }
})
