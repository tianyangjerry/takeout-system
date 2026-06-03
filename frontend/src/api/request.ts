import axios from 'axios'
import type { AxiosRequestConfig } from 'axios'
import type { ApiResponse } from '../types/api'
import { clearStoredSession, getStoredToken } from '../stores/authStore'

export const request = axios.create({
  baseURL: 'http://localhost:8090/api',
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const token = getStoredToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isAxiosError(error)) {
      const status = error.response?.status
      if (status === 401) {
        clearStoredSession()
        if (window.location.pathname !== '/login') {
          window.location.assign('/login')
        }
      }
      if (status === 403) {
        window.dispatchEvent(new CustomEvent('api-forbidden'))
      }
    }
    return Promise.reject(error)
  },
)

function getErrorMessage(error: unknown) {
  if (axios.isAxiosError<ApiResponse<unknown>>(error)) {
    return error.response?.data?.message || error.message || '请求失败'
  }

  if (error instanceof Error) {
    return error.message
  }

  return '请求失败'
}

async function unwrap<T>(promise: Promise<{ data: ApiResponse<T> }>) {
  try {
    const response = await promise
    if (response.data.code !== 200) {
      throw new Error(response.data.message || '请求失败')
    }
    return response.data.data
  } catch (error) {
    throw new Error(getErrorMessage(error), { cause: error })
  }
}

export function apiGet<T>(url: string, config?: AxiosRequestConfig) {
  return unwrap<T>(request.get<ApiResponse<T>>(url, config))
}

export function apiPost<T, B = unknown>(url: string, data?: B, config?: AxiosRequestConfig) {
  return unwrap<T>(request.post<ApiResponse<T>>(url, data, config))
}

export function apiPut<T, B = unknown>(url: string, data?: B, config?: AxiosRequestConfig) {
  return unwrap<T>(request.put<ApiResponse<T>>(url, data, config))
}

export function apiPatch<T, B = unknown>(url: string, data?: B, config?: AxiosRequestConfig) {
  return unwrap<T>(request.patch<ApiResponse<T>>(url, data, config))
}

export function apiDelete<T>(url: string, config?: AxiosRequestConfig) {
  return unwrap<T>(request.delete<ApiResponse<T>>(url, config))
}
