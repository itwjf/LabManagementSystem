// src/api/user.js
// 用户相关API
import axios from 'axios'

// 获取所有用户
// 用于下拉选择功能（如采购人、经办人等）
export function getAllUsers() {
  return axios.get('/api/users')
}
