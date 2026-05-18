import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import App from './App.vue'
import router from './router'
import { setupStore } from './store'
import { initPreferences } from '@/utils/preferences'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

/* 引入Element Plus样式 */
import 'element-plus/dist/index.css'
/* 引入Element Plus暗色主题 */
import 'element-plus/theme-chalk/dark/css-vars.css'
/* 引入NProgress样式 */
import 'nprogress/nprogress.css'
/* 引入设计令牌（亮色/暗色/语义化） */
import '@/styles/design-tokens/default.css'
import '@/styles/design-tokens/dark.css'
import '@/styles/design-tokens/semantic.css'
/* 引入全局样式 */
import '@/styles/index.scss'

const app = createApp(App)

/* 注册Element Plus图标 */
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

/* 初始化偏好设置系统 */
initPreferences({
  namespace: 'wms-web',
  overrides: {
    app: {
      name: '备品备件库房管理平台',
    },
  },
})

setupStore(app)
app.use(ElementPlus, { locale: zhCn })
app.use(router)
app.mount('#app')
