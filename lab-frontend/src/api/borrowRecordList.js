// src/api/borrowRecordList.js
import axios from 'axios'
import router from '@/router'
import { ElMessage } from 'element-plus'

// 创建专用 axios 实例（带拦截器）
const borrowRecordApi = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：加 token
borrowRecordApi.interceptors.request.use(config => {
  const token = localStorage.getItem('authToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => Promise.reject(error))

// 响应拦截器：提取 data.data
borrowRecordApi.interceptors.response.use(
  response => {
    const data = response.data

    // 用户信息接口（避免干扰）
    if (response.config.url.includes('/api/auth/me')) {
      return data
    }

    // 标准业务接口：自动提取 data.data
    if (data && data.code === 200) {
      return data.data // ← 关键：返回 IPage<DeviceBorrow>
    }

    return data
  },
  error => {
    const status = error.response?.status
    const msg = error.response?.data?.message || '网络错误'

    if (status === 401 || status === 403) {
      localStorage.removeItem('authToken')
      localStorage.removeItem('currentUser')
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    }

    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

// 导出接口方法
export function getMyBorrowRecords(page = 1, size = 100) {
  return borrowRecordApi.get('/device-borrows/my', { params: { page, size } })
}

export function getAllBorrowRecords(page = 1, size = 100) {
  return borrowRecordApi.get('/device-borrows', { params: { page, size } })
}