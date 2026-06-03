import type { DishStatus } from '../types/dish'
import type { OrderStatus } from '../types/order'
import { getOrderStatusLabel } from '../utils/status'

const orderClassName: Record<OrderStatus, string> = {
  PENDING: 'bg-amber-100 text-amber-700',
  ACCEPTED: 'bg-blue-100 text-blue-700',
  COOKING: 'bg-orange-100 text-orange-700',
  DELIVERING: 'bg-cyan-100 text-cyan-700',
  COMPLETED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-stone-200 text-stone-600',
}

export function OrderStatusBadge({ status }: { status: OrderStatus }) {
  return (
    <span className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${orderClassName[status]}`}>
      {getOrderStatusLabel(status)}
    </span>
  )
}

export function DishStatusBadge({ status }: { status: DishStatus }) {
  return (
    <span
      className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${
        status === 1 ? 'bg-green-100 text-green-700' : 'bg-stone-200 text-stone-600'
      }`}
    >
      {status === 1 ? '上架' : '下架'}
    </span>
  )
}

export function StockBadge({ stock }: { stock: number }) {
  const className =
    stock === 0
      ? 'bg-red-100 text-red-700'
      : stock < 10
        ? 'bg-amber-100 text-amber-700'
        : 'bg-green-100 text-green-700'

  return <span className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${className}`}>库存 {stock}</span>
}
