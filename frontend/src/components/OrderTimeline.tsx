import type { OrderStatus, OrderTimelineItem } from '../types/order'
import { getOrderStatusLabel, orderFlow } from '../utils/status'

interface OrderTimelineProps {
  status: OrderStatus
  timeline?: OrderTimelineItem[]
}

export function OrderTimeline({ status, timeline }: OrderTimelineProps) {
  const fallback = orderFlow.map((item) => ({
    status: item,
    label: getOrderStatusLabel(item),
    active: orderFlow.indexOf(item) <= orderFlow.indexOf(status),
    time: null,
  }))
  const items = timeline?.length ? timeline : fallback

  return (
    <div className="grid gap-3 md:grid-cols-5">
      {items.map((item, index) => (
        <div key={`${item.status}-${index}`} className="rounded-lg border border-orange-100 bg-white p-4">
          <div
            className={`mb-3 flex h-8 w-8 items-center justify-center rounded-full text-sm font-bold ${
              item.active ? 'bg-orange-500 text-white' : 'bg-stone-100 text-stone-400'
            }`}
          >
            {index + 1}
          </div>
          <p className="font-semibold text-stone-900">{item.label}</p>
          <p className="mt-1 text-xs text-stone-500">{item.time || '等待更新'}</p>
        </div>
      ))}
    </div>
  )
}
