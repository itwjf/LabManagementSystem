// src/composables/useAuth.js
import { ref } from 'vue'

export function useAuth() {
  const currentUser = ref(null)

  // 从 localStorage 的 token 解析用户信息（无需网络请求）
  const fetchCurrentUser = () => {
    const token = localStorage.getItem('authToken')
    if (!token) {
      currentUser.value = null
      return null
    }

    try {
      // 解析 JWT payload
      const payloadBase64 = token.split('.')[1]
      const payloadJson = atob(payloadBase64.replace(/-/g, '+').replace(/_/g, '/'))
      const payload = JSON.parse(payloadJson)

      const user = {
        username: payload.sub,
        role: payload.role
        // 注意：这里没有 id、realName 等字段
        // 如果需要，可后续扩展
      }

      currentUser.value = user
      return user
    } catch (err) {
      console.error('解析 token 失败:', err)
      currentUser.value = null
      return null
    }
  }

  const logout = () => {
    localStorage.removeItem('authToken')
    currentUser.value = null
  }

  return {
    currentUser,
    fetchCurrentUser,
    logout
  }
}