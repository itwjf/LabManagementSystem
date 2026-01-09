// src/api/labInfo.js
// 实训室管理相关API
import axios from 'axios'

// 获取所有实训室信息
export function getAllLabs() {
  return axios.get('/api/lab-info')
}

// 根据ID获取实训室信息
export function getLabById(id) {
  return axios.get(`/api/lab-info/${id}`)
}

// 创建实训室信息
export function createLab(data) {
  return axios.post('/api/lab-info', data)
}

// 更新实训室信息
export function updateLab(id, data) {
  return axios.put(`/api/lab-info/${id}`, data)
}

// 删除实训室信息
export function deleteLab(id) {
  return axios.delete(`/api/lab-info/${id}`)
}

// 分页查询实训室信息（支持筛选）
export function getLabsPage(page = 1, size = 10, filters = {}) {
  const { labName, location, status } = filters
  return axios.get('/api/lab-info/page', {
    params: {
      page,
      size,
      labName: labName || undefined,
      location: location || undefined,
      status: status || undefined
    }
  })
}