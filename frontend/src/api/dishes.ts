import { apiDelete, apiGet, apiPatch, apiPost, apiPut } from './request'
import type { PageResult } from '../types/api'
import type { Dish, DishPayload, DishQuery, UploadImageResponse } from '../types/dish'

export const dishApi = {
  list: (params?: DishQuery) => apiGet<PageResult<Dish>>('/dishes', { params }),
  detail: (id: number) => apiGet<Dish>(`/dishes/${id}`),
  top: (limit = 5) => apiGet<Dish[]>('/dishes/top', { params: { limit } }),
  recommend: (limit = 6) => apiGet<Dish[]>('/dishes/recommend', { params: { limit } }),
  create: (payload: DishPayload) => apiPost<{ id: number }, DishPayload>('/admin/dishes', payload),
  update: (id: number, payload: DishPayload) =>
    apiPut<null, DishPayload>(`/admin/dishes/${id}`, payload),
  updateStatus: (id: number, status: DishPayload['status']) =>
    apiPatch<null, { status: DishPayload['status'] }>(`/admin/dishes/${id}/status`, { status }),
  remove: (id: number) => apiDelete<null>(`/admin/dishes/${id}`),
  uploadImage: (file: File) => {
    const form = new FormData()
    form.append('file', file)
    return apiPost<UploadImageResponse, FormData>('/upload/image', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}
