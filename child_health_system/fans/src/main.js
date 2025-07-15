import { createApp } from 'vue'
import ElementPlus from 'element-plus'
// pinia
import { createPinia } from 'pinia'
//导入持久化插件
import {createPersistedState} from'pinia-persistedstate-plugin'
import * as ElementPlusIcons from '@element-plus/icons-vue'

import 'element-plus/dist/index.css'
import App from './App.vue'
//导入路由
import router from '@/router'
import { permission } from './directives/permission'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIcons)) {
  app.component(key, component)
}

const pinia = createPinia()
const persist = createPersistedState()
app.use(pinia)
//pinia使用持久化插件
pinia.use(persist)
//挂载路由

app.use(router)
app.use(ElementPlus, { zIndex: 3000 })
app.directive('permission', permission)
app.mount('#app')