// src/api/assetInRecord.js
// 资产入库记录相关API
import axios from 'axios'

// 获取所有资产入库记录
export function getAllInRecords() {
  return axios.get('/api/asset-in-records')
}

// 根据ID获取资产入库记录
export function getInRecordById(id) {
  return axios.get(`/api/asset-in-records/${id}`)
}

// 创建资产入库记录
export function createInRecord(data) {
  return axios.post('/api/asset-in-records', data)
}

// 删除资产入库记录
export function deleteInRecord(id) {
  return axios.delete(`/api/asset-in-records/${id}`)
}

// 分页查询资产入库记录（支持筛选）
export function getInRecordsPage(page = 1, size = 10, filters = {}) {
  const { assetId, assetName, labId, purchaserName, inDateStart, inDateEnd } = filters
  return axios.get('/api/asset-in-records/page', {
    params: {
      page,
      size,
      assetId: assetId || undefined,
      assetName: assetName || undefined,
      labId: labId || undefined,
      purchaserName: purchaserName || undefined,
      inDateStart: inDateStart || undefined,
      inDateEnd: inDateEnd || undefined
    }
  })
}