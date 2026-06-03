import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useParams } from 'react-router-dom'
import { orderApi } from '../../api/orders'
import { OrderTimeline } from '../../components/OrderTimeline'
import { ErrorState, LoadingBlock, LoginRequired } from '../../components/PageState'
import { OrderStatusBadge } from '../../components/StatusBadge'
import { useAuthStore } from '../../stores/authStore'
import { formatMoney } from '../../utils/format'

export function OrderDetailPage() {
  const { token, user } = useAuthStore()
  const params = useParams()
  const id = Number(params.id)
  const queryClient = useQueryClient()
  const order = useQuery({
    queryKey: ['order', id, user?.role],
    queryFn: () => (user?.role === 'ADMIN' ? orderApi.adminDetail(id) : orderApi.detail(id)),
    enabled: Boolean(token && Number.isFinite(id)),
  })
  const cancelOrder = useMutation({
    mutationFn: () => orderApi.cancel(id, '用户取消订单'),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['order', id] })
      void queryClient.invalidateQueries({ queryKey: ['orders'] })
    },
  })

  if (!token) {
    return <LoginRequired />
  }

  if (order.isLoading) {
    return <LoadingBlock />
  }

  if (order.isError) {
    return <ErrorState message={order.error.message} />
  }

  if (!order.data) {
    return <ErrorState title="订单不存在" />
  }

  return (
    <div className="space-y-6">
      <div className="rounded-lg border border-orange-100 bg-white p-6 shadow-sm">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <p className="text-sm text-stone-500">订单编号</p>
            <h1 className="mt-1 text-2xl font-bold text-stone-950">{order.data.orderNo}</h1>
          </div>
          <OrderStatusBadge status={order.data.status} />
        </div>
        <div className="mt-5 grid gap-4 md:grid-cols-3">
          <div>
            <p className="text-sm text-stone-500">收货人</p>
            <p className="mt-1 font-semibold">{order.data.receiverName}</p>
          </div>
          <div>
            <p className="text-sm text-stone-500">手机号</p>
            <p className="mt-1 font-semibold">{order.data.receiverPhone}</p>
          </div>
          <div>
            <p className="text-sm text-stone-500">金额</p>
            <p className="mt-1 font-bold text-orange-600">{formatMoney(order.data.totalAmount)}</p>
          </div>
        </div>
        <p className="mt-4 text-sm text-stone-500">{order.data.receiverAddress}</p>
        {order.data.remark ? <p className="mt-2 text-sm text-stone-500">备注：{order.data.remark}</p> : null}
        {cancelOrder.isError ? <div className="mt-4"><ErrorState message={cancelOrder.error.message} /></div> : null}
        {user?.role !== 'ADMIN' && order.data.status === 'PENDING' ? (
          <button
            type="button"
            disabled={cancelOrder.isPending}
            onClick={() => cancelOrder.mutate()}
            className="mt-5 rounded-lg border border-red-100 bg-red-50 px-4 py-2 text-sm font-semibold text-red-600"
          >
            取消订单
          </button>
        ) : null}
      </div>
      <section>
        <h2 className="mb-4 text-xl font-bold text-stone-950">订单进度</h2>
        <OrderTimeline status={order.data.status} timeline={order.data.timeline} />
      </section>
      <section className="rounded-lg border border-orange-100 bg-white p-5 shadow-sm">
        <h2 className="text-xl font-bold text-stone-950">菜品明细</h2>
        <div className="mt-4 space-y-3">
          {order.data.items.map((item) => (
            <div key={item.id} className="flex flex-wrap items-center justify-between gap-3 border-b border-orange-50 py-3 last:border-0">
              <div>
                <p className="font-semibold">{item.dishName}</p>
                <p className="mt-1 text-sm text-stone-500">
                  {formatMoney(item.dishPrice)} × {item.quantity}
                </p>
              </div>
              <span className="font-bold text-stone-950">{formatMoney(item.subtotal)}</span>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}
