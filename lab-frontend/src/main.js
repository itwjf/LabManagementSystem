import { createApp } from 'vue'
import App from './App.vue' // 原始入口组件
import router from './router' //  引入你写的路由
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
// 👇 引入所有图标（推荐用于小型项目）
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 创建 Vue 应用
const app = createApp(App)

// 全局配置：自动附加 JWT 并处理 401/403
// 请求拦截器：加 token
axios.interceptors.request.use(config => {
	const token = localStorage.getItem('authToken')
	if (token) {
		config.headers = config.headers || {}
		config.headers.Authorization = `Bearer ${token}`
	}
	return config
})

// 响应拦截器：统一处理 Result 结构 + 错误跳转
axios.interceptors.response.use(
	  response => {
	const data = response.data
    console.log('【拦截器收到原始 data】:', data)

    // 登录接口
    if (data && typeof data.token === 'string') {
      console.log('→ 识别为登录接口')
      return data
    }

    // 用户信息接口：必须显式判断路径！
    if (response.config.url.includes('/api/auth/me')) {
      console.log('→ 识别为用户信息接口')
      return data // 直接返回整个 data
    }

    // 其他接口使用 code/data 结构
    if (data && data.code === 200) {
      console.log('→ 识别为标准业务接口')
      return data.data
    }

    // 兜底
    console.warn('→ 未知响应格式:', data)
    return data
  },
	error => {
		const status = error.response?.status
		const msg = error.response?.data?.message || '网络错误'
		if (status === 401 || status === 403) {
			localStorage.removeItem('authToken')
			if (router.currentRoute.value.path !== '/login') {
				router.replace('/login')
			}
		}
		//  提供更友好的网络错误提示
    	const errorMsg = error.response?.data?.message 
      	|| (status === 401 ? '用户名或密码错误' : '网络连接失败')
		return Promise.reject(new Error(errorMsg))
	}
)

// 使用路由（关键一步）
app.use(router)

// 使用 Element Plus UI
app.use(ElementPlus)

// 全局注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.config.globalProperties.$http = axios // 可选：全局挂载 axios

// 挂载到 #app 元素
app.mount('#app')