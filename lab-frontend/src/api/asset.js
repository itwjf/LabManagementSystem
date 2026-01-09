// src/api/asset.js
// 固定资产管理相关API
import axios from 'axios'

// 获取所有资产
export function getAllAssets() {
  return axios.get('/api/assets')
}

// 根据ID获取资产
export function getAssetById(id) {
  return axios.get(`/api/assets/${id}`)
}

// 创建资产
export function createAsset(data) {
  return axios.post('/api/assets', data)
}

// 更新资产
export function updateAsset(id, data) {
  return axios.put(`/api/assets/${id}`, data)
}

// 删除资产
export function deleteAsset(id) {
  return axios.delete(`/api/assets/${id}`)
}

// 分页查询资产（支持筛选）
export function getAssetsPage(page = 1, size = 10, filters = {}) {
  const { assetName, typeId, labId, status } = filters
  return axios.get('/api/assets/page', {
    params: {
      page,
      size,
      assetName: assetName || undefined,
      typeId: typeId || undefined,
      labId: labId || undefined,
      status: status || undefined
    }
  })
}