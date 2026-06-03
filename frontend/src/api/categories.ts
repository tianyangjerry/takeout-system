import { apiDelete, apiGet, apiPost, apiPut } from './request'
import type { Category, CategoryPayload } from '../types/category'

export const categoryApi = {
  list: () => apiGet<Category[]>('/categories'),
  create: (payload: CategoryPayload) =>
    apiPost<Category, CategoryPayload>('/admin/categories', payload),
  update: (id: number, payload: CategoryPayload) =>
    apiPut<null, CategoryPayload>(`/admin/categories/${id}`, payload),
  remove: (id: number) => apiDelete<null>(`/admin/categories/${id}`),
}
