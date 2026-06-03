import { apiDelete, apiGet, apiPost, apiPut } from './request'
import type { AddCartItemRequest, Cart, UpdateCartItemRequest } from '../types/cart'

export const cartApi = {
  get: () => apiGet<Cart>('/cart'),
  addItem: (payload: AddCartItemRequest) => apiPost<null, AddCartItemRequest>('/cart/items', payload),
  updateItem: (id: number, payload: UpdateCartItemRequest) =>
    apiPut<null, UpdateCartItemRequest>(`/cart/items/${id}`, payload),
  removeItem: (id: number) => apiDelete<null>(`/cart/items/${id}`),
  clear: () => apiDelete<null>('/cart'),
}
