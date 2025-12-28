import { createApp } from 'vue'
import App from './App.vue' // 原始入口组件
import router from './router' // ★ 引入你写的路由
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

// 创建 Vue 应用
const app = createApp(App)

// 全局配置：自动附加 JWT 并处理 401/403
axios.interceptors.request.use(config => {
	const token = localStorage.getItem('authToken')
	if (token) {
		config.headers = config.headers || {}
		config.headers.Authorization = `Bearer ${token}`
	}
	return config
})

axios.interceptors.response.use(
	response => response,
	error => {
		const status = error.response?.status
		if (status === 401 || status === 403) {
			localStorage.removeItem('authToken')
			if (router.currentRoute.value.path !== '/login') {
				router.replace('/login')
			}
		}
		return Promise.reject(error)
	}
)

// 使用路由（关键一步）
app.use(router)

// 使用 Element Plus UI
app.use(ElementPlus)

// 挂载到 #app 元素
app.mount('#app')