import { useQuery } from '@tanstack/react-query'
import { Link, useSearchParams } from 'react-router-dom'
import { orderApi } from '../../api/orders'
import { EmptyState, ErrorState, LoadingBlock, LoginRequired } from '../../components/PageState'
import { OrderStatusBadge } from '../../components/StatusBadge'
import { useAuthStore } from '../../stores/authStore'
import type { OrderStatus } from '../../types/order'
import { formatMoney } from '../../utils/format'
import { orderStatusOptions } from '../../utils/status'

export function OrdersPage() {
  const token = useAuthStore((state) => state.token)
  const [searchParams, setSearchParams] = useSearchParams()
  const status = searchParams.get('status') as OrderStatus | null
  const orders = useQuery({
    queryKey: ['orders', 'my', status],
    queryFn: () => orderApi.my({ status: status || undefined, page: 1, pageSize: 20 }),
    enabled: Boolean(token),
  })

  if (!token) {
    return <LoginRequired />
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-stone-950">我的订单</h1>
        <p className="mt-2 text-stone-500">查看订单状态和配送进度。</p>
      </div>
      <div className="flex flex-wrap gap-2">
        <button
          type="button"
          onClick={() => setSearchParams({})}
          className={`rounded-lg px-4 py-2 text-sm font-semibold ${status ? 'bg-white text-stone-600' : 'bg-orange-500 text-white'}`}
        >
          全部
        </button>
        {orderStatusOptions.map((item) => (
          <button
            key={item.value}
            type="button"
            onClick={() => setSearchParams({ status: item.value })}
            className={`rounded-lg px-4 py-2 text-sm font-semibold ${
              status === item.value ? 'bg-orange-500 text-white' : 'bg-white text-stone-600'
            }`}
          >
            {item.label}
          </button>
        ))}
      </div>
      {orders.isLoading ? <LoadingBlock /> : null}
      {orders.isError ? <ErrorState message={orders.error.message} /> : null}
      {orders.data?.records.length ? (
        <div className="space-y-3">
          {orders.data.records.map((order) => (
            <Link key={order.id} to={`/orders/${order.id}`} className="block rounded-lg border border-orange-100 bg-white p-5 shadow-sm hover:border-orange-300">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <p className="font-semibold text-stone-950">{order.orderNo}</p>
                  <p className="mt-1 text-sm text-stone-500">{order.createdTime}</p>
                </div>
                <OrderStatusBadge status={order.status} />
              </div>
              <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
                <span className="text-sm text-stone-500">菜品数量 {order.itemCount ?? '-'}</span>
                <span className="text-xl font-bold text-orange-600">{formatMoney(order.totalAmount)}</span>
              </div>
            </Link>
          ))}
        </div>
      ) : orders.data ? (
        <EmptyState title="暂无订单" message="提交订单后会出现在这里。" />
      ) : null}
    </div>
  )
}
