import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import SvgIcon from '@/components/common/SvgIcon.vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import { ElMessage } from 'element-plus'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.component('SvgIcon', SvgIcon)

app.config.errorHandler = (err, instance, info) => {
  console.error('Global error:', err, info)
  ElMessage.error('页面出现错误，请刷新重试')
}

app.use(ElementPlus, { locale: zhCn })
app.use(createPinia())
app.use(router)
app.mount('#app')
