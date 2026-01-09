// src/api/assetOutRecord.js
// 资产出库记录相关API
import axios from 'axios'

// 获取所有资产出库记录
export function getAllOutRecords() {
  return axios.get('/api/asset-out-records')
}

// 根据ID获取资产出库记录
export function getOutRecordById(id) {
  return axios.get(`/api/asset-out-records/${id}`)
}

// 创建资产出库记录
export function createOutRecord(data) {
  return axios.post('/api/asset-out-records', data)
}

// 删除资产出库记录
export function deleteOutRecord(id) {
  return axios.delete(`/api/asset-out-records/${id}`)
}

// 分页查询资产出库记录（支持筛选）
export function getOutRecordsPage(page = 1, size = 10, filters = {}) {
  const { assetId, assetName, labId, outReason, approvalStatus, outDateStart, outDateEnd } = filters
  return axios.get('/api/asset-out-records/page', {
    params: {
      page,
      size,
      assetId: assetId || undefined,
      assetName: assetName || undefined,
      labId: labId || undefined,
      outReason: outReason || undefined,
      approvalStatus: approvalStatus || undefined,
      outDateStart: outDateStart || undefined,
      outDateEnd: outDateEnd || undefined
    }
  })
}