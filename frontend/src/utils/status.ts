import type { OrderStatus } from '../types/order'

export const orderStatusOptions: Array<{ value: OrderStatus; label: string }> = [
  { value: 'PENDING', label: '待接单' },
  { value: 'ACCEPTED', label: '已接单' },
  { value: 'COOKING', label: '制作中' },
  { value: 'DELIVERING', label: '配送中' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'CANCELLED', label: '已取消' },
]

export const orderFlow: OrderStatus[] = [
  'PENDING',
  'ACCEPTED',
  'COOKING',
  'DELIVERING',
  'COMPLETED',
]

export function getOrderStatusLabel(status: OrderStatus) {
  return orderStatusOptions.find((item) => item.value === status)?.label ?? status
}

export function getNextOrderStatus(status: OrderStatus) {
  const index = orderFlow.indexOf(status)
  return index >= 0 ? orderFlow[index + 1] : undefined
}
