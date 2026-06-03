import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { FormEvent } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { orderApi } from '../../api/orders'
import { EmptyState, ErrorState, LoadingBlock } from '../../components/PageState'
import { SectionTitle } from '../../components/SectionTitle'
import { OrderStatusBadge } from '../../components/StatusBadge'
import type { OrderStatus } from '../../types/order'
import { formatMoney } from '../../utils/format'
import { getNextOrderStatus, getOrderStatusLabel, orderStatusOptions } from '../../utils/status'

export function AdminOrdersPage() {
  const queryClient = useQueryClient()
  const [searchParams, setSearchParams] = useSearchParams()
  const keyword = searchParams.get('keyword') || ''
  const status = searchParams.get('status') as OrderStatus | null
  const startDate = searchParams.get('startDate') || ''
  const endDate = searchParams.get('endDate') || ''
  const orders = useQuery({
    queryKey: ['admin', 'orders', keyword, status, startDate, endDate],
    queryFn: () => orderApi.adminList({
      keyword: keyword || undefined,
      status: status || undefined,
      startDate: startDate || undefined,
      endDate: endDate || undefined,
      page: 1,
      pageSize: 30,
    }),
  })
  const updateStatus = useMutation({
    mutationFn: ({ id, next }: { id: number; next: OrderStatus }) => orderApi.updateStatus(id, next),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['admin', 'orders'] })
      void queryClient.invalidateQueries({ queryKey: ['order'] })
    },
  })

  function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const next = new URLSearchParams()
    const nextKeyword = String(form.get('keyword') || '').trim()
    const nextStatus = String(form.get('status') || '')
    const nextStartDate = String(form.get('startDate') || '')
    const nextEndDate = String(form.get('endDate') || '')
    if (nextKeyword) {
      next.set('keyword', nextKeyword)
    }
    if (nextStatus) {
      next.set('status', nextStatus)
    }
    if (nextStartDate) {
      next.set('startDate', nextStartDate)
    }
    if (nextEndDate) {
      next.set('endDate', nextEndDate)
    }
    setSearchParams(next)
  }

  return (
    <div className="space-y-6">
      <SectionTitle title="订单管理" extra="按正常状态流转处理订单" />
      <form onSubmit={handleSearch} className="grid gap-3 rounded-lg border border-stone-200 bg-white p-4 shadow-sm lg:grid-cols-[1fr_170px_160px_160px_auto]">
        <input name="keyword" defaultValue={keyword} placeholder="订单号、用户名、手机号" className="rounded-lg border border-stone-200 px-4 py-2.5" />
        <select name="status" defaultValue={status || ''} className="rounded-lg border border-stone-200 px-4 py-2.5">
          <option value="">全部状态</option>
          {orderStatusOptions.map((item) => (
            <option key={item.value} value={item.value}>
              {item.label}
            </option>
          ))}
        </select>
        <input name="startDate" type="date" defaultValue={startDate} className="rounded-lg border border-stone-200 px-4 py-2.5" />
        <input name="endDate" type="date" defaultValue={endDate} className="rounded-lg border border-stone-200 px-4 py-2.5" />
        <button type="submit" className="rounded-lg bg-orange-500 px-5 py-2.5 font-semibold text-white">
          查询
        </button>
      </form>
      {updateStatus.isError ? <ErrorState message={updateStatus.error.message} /> : null}
      {orders.isLoading ? <LoadingBlock /> : null}
      {orders.isError ? <ErrorState message={orders.error.message} /> : null}
      {orders.data?.records.length ? (
        <div className="overflow-x-auto rounded-lg border border-stone-200 bg-white shadow-sm">
          <table className="w-full min-w-[980px] text-left text-sm">
            <thead className="bg-stone-50 text-stone-500">
              <tr>
                <th className="px-4 py-3">订单号</th>
                <th className="px-4 py-3">用户</th>
                <th className="px-4 py-3">金额</th>
                <th className="px-4 py-3">收货人</th>
                <th className="px-4 py-3">地址</th>
                <th className="px-4 py-3">状态</th>
                <th className="px-4 py-3">时间</th>
                <th className="px-4 py-3">操作</th>
              </tr>
            </thead>
            <tbody>
              {orders.data.records.map((order) => {
                const next = getNextOrderStatus(order.status)
                return (
                  <tr key={order.id} className="border-t border-stone-100">
                    <td className="px-4 py-3 font-semibold">{order.orderNo}</td>
                    <td className="px-4 py-3">{order.username || '-'}</td>
                    <td className="px-4 py-3">{formatMoney(order.totalAmount)}</td>
                    <td className="px-4 py-3">{order.receiverName || '-'}</td>
                    <td className="max-w-[220px] truncate px-4 py-3">{order.receiverAddress || '-'}</td>
                    <td className="px-4 py-3"><OrderStatusBadge status={order.status} /></td>
                    <td className="px-4 py-3">{order.createdTime}</td>
                    <td className="px-4 py-3">
                      <div className="flex flex-wrap gap-2">
                        <Link to={`/admin/orders/${order.id}`} className="rounded-lg border border-stone-200 px-3 py-2 text-xs font-semibold text-stone-700">
                          详情
                        </Link>
                        {next ? (
                          <button
                            type="button"
                            disabled={updateStatus.isPending}
                            onClick={() => updateStatus.mutate({ id: order.id, next })}
                            className="rounded-lg bg-orange-500 px-3 py-2 text-xs font-semibold text-white disabled:bg-stone-300"
                          >
                            改为{getOrderStatusLabel(next)}
                          </button>
                        ) : (
                          <span className="px-3 py-2 text-xs text-stone-400">不可流转</span>
                        )}
                      </div>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      ) : orders.data ? (
        <EmptyState />
      ) : null}
    </div>
  )
}
