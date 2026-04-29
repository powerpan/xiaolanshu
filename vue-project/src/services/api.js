import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 12000,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwttoken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const getToken = () => localStorage.getItem('jwttoken') || ''

export const withToken = (params = {}) => ({
  ...params,
})

export const formBody = (params = {}) => {
  const body = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      body.append(key, value)
    }
  })
  return body
}

export async function unwrap(request) {
  const response = await request
  const payload = response.data
  if (!payload || payload.status !== 1) {
    throw new Error(payload?.message || '请求失败')
  }
  return payload.data
}

export default api
