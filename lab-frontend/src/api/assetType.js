// src/api/assetType.js
// 资产类型管理相关API
import axios from 'axios'

// 获取所有资产类型
export function getAllAssetTypes() {
  return axios.get('/api/asset-types')
}

// 根据ID获取资产类型
export function getAssetTypeById(id) {
  return axios.get(`/api/asset-types/${id}`)
}

// 创建资产类型
export function createAssetType(data) {
  return axios.post('/api/asset-types', data)
}

// 更新资产类型
export function updateAssetType(id, data) {
  return axios.put(`/api/asset-types/${id}`, data)
}

// 删除资产类型
export function deleteAssetType(id) {
  return axios.delete(`/api/asset-types/${id}`)
}

// 分页查询资产类型（支持筛选）
export function getAssetTypesPage(page = 1, size = 10, typeName = '') {
  return axios.get('/api/asset-types/page', {
    params: {
      page,
      size,
      typeName: typeName || undefined
    }
  })
}