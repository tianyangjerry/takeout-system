import { apiGet, apiPatch, apiPost } from './request'
import type { PageResult } from '../types/api'
import type {
  AdminOrderQuery,
  CreateOrderRequest,
  CreateOrderResponse,
  MyOrderQuery,
  OrderDetail,
  OrderStatus,
  OrderSummary,
} from '../types/order'

export const orderApi = {
  create: (payload: CreateOrderRequest) =>
    apiPost<CreateOrderResponse, CreateOrderRequest>('/orders', payload),
  my: (params?: MyOrderQuery) => apiGet<PageResult<OrderSummary>>('/orders/my', { params }),
  detail: (id: number) => apiGet<OrderDetail>(`/orders/${id}`),
  adminDetail: (id: number) => apiGet<OrderDetail>(`/admin/orders/${id}`),
  cancel: (id: number, reason: string) =>
    apiPatch<null, { reason: string }>(`/orders/${id}/cancel`, { reason }),
  adminList: (params?: AdminOrderQuery) =>
    apiGet<PageResult<OrderSummary>>('/admin/orders', { params }),
  updateStatus: (id: number, status: OrderStatus) =>
    apiPatch<null, { status: OrderStatus }>(`/admin/orders/${id}/status`, { status }),
}
