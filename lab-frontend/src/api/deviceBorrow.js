// src/api/deviceBorrow.js
import axios from 'axios'

export function borrowDevice(data) {
  return axios.post('/api/device-borrows/borrow', data)
}

export function returnDevice(data) {
  return axios.post('/api/device-borrows/return', data)
}

export function getMyBorrows(page = 1, size = 10) {
  return axios.get('/api/device-borrows/my', { params: { page, size } })
}