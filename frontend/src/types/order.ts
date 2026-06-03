import type { PageQuery } from './api'

export type OrderStatus =
  | 'PENDING'
  | 'ACCEPTED'
  | 'COOKING'
  | 'DELIVERING'
  | 'COMPLETED'
  | 'CANCELLED'

export interface OrderItem {
  id: number
  dishId: number
  dishName: string
  dishPrice: number
  quantity: number
  subtotal: number
}

export interface OrderTimelineItem {
  status: OrderStatus
  label: string
  active: boolean
  time: string | null
}

export interface OrderSummary {
  id: number
  orderNo: string
  username?: string
  totalAmount: number
  receiverName?: string
  receiverPhone?: string
  receiverAddress?: string
  status: OrderStatus
  statusText?: string
  createdTime: string
  itemCount?: number
}

export interface OrderDetail extends OrderSummary {
  userId?: number
  remark?: string
  updatedTime?: string
  items: OrderItem[]
  timeline?: OrderTimelineItem[]
}

export interface CreateOrderRequest {
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  remark?: string
}

export interface CreateOrderResponse {
  orderId: number
  orderNo: string
  totalAmount: number
  status: OrderStatus
}

export interface MyOrderQuery extends PageQuery {
  status?: OrderStatus
}

export interface AdminOrderQuery extends PageQuery {
  keyword?: string
  status?: OrderStatus
  startDate?: string
  endDate?: string
}
