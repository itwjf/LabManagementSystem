import { createApp } from 'vue'
import App from './App.vue' // 原始入口组件
import router from './router' // ★ 引入你写的路由
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 创建 Vue 应用
const app = createApp(App)

// 使用路由（关键一步）
app.use(router)

// 使用 Element Plus UI
app.use(ElementPlus)

// 挂载到 #app 元素
app.mount('#app')