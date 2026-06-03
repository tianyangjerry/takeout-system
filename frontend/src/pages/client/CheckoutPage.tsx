import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { cartApi } from '../../api/cart'
import { orderApi } from '../../api/orders'
import { EmptyState, ErrorState, LoadingBlock, LoginRequired } from '../../components/PageState'
import { useAuthStore } from '../../stores/authStore'
import { formatMoney } from '../../utils/format'

const quickNotes = ['少辣', '不要香菜', '多米饭', '尽快送达', '需要餐具', '不需要餐具']

export function CheckoutPage() {
  const { token, user } = useAuthStore()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const cart = useQuery({ queryKey: ['cart'], queryFn: cartApi.get, enabled: Boolean(token) })
  const createOrder = useMutation({
    mutationFn: orderApi.create,
    onSuccess: (data) => {
      void queryClient.invalidateQueries({ queryKey: ['cart'] })
      navigate(`/orders/${data.orderId}`)
    },
  })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    createOrder.mutate({
      receiverName: String(form.get('receiverName') || ''),
      receiverPhone: String(form.get('receiverPhone') || ''),
      receiverAddress: String(form.get('receiverAddress') || ''),
      remark: String(form.getAll('notes').concat(String(form.get('remark') || '')).filter(Boolean).join('，')),
    })
  }

  if (!token) {
    return <LoginRequired />
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-stone-950">提交订单</h1>
        <p className="mt-2 text-stone-500">填写收货信息并确认备注。</p>
      </div>
      {cart.isLoading ? <LoadingBlock /> : null}
      {cart.isError ? <ErrorState message={cart.error.message} /> : null}
      {cart.data && cart.data.items.length === 0 ? <EmptyState title="购物车为空" message="添加菜品后再提交订单。" /> : null}
      {cart.data?.items.length ? (
        <form onSubmit={handleSubmit} className="grid gap-6 lg:grid-cols-[1fr_340px]">
          <div className="space-y-4 rounded-lg border border-orange-100 bg-white p-5 shadow-sm">
            <label className="block">
              <span className="text-sm font-semibold text-stone-700">收货人</span>
              <input
                name="receiverName"
                required
                defaultValue={user?.username}
                className="mt-2 w-full rounded-lg border border-stone-200 px-4 py-3 outline-orange-400"
              />
            </label>
            <label className="block">
              <span className="text-sm font-semibold text-stone-700">手机号</span>
              <input
                name="receiverPhone"
                required
                defaultValue={user?.phone}
                className="mt-2 w-full rounded-lg border border-stone-200 px-4 py-3 outline-orange-400"
              />
            </label>
            <label className="block">
              <span className="text-sm font-semibold text-stone-700">收货地址</span>
              <input
                name="receiverAddress"
                required
                defaultValue={user?.address}
                className="mt-2 w-full rounded-lg border border-stone-200 px-4 py-3 outline-orange-400"
              />
            </label>
            <fieldset>
              <legend className="text-sm font-semibold text-stone-700">快捷备注</legend>
              <div className="mt-3 flex flex-wrap gap-2">
                {quickNotes.map((note) => (
                  <label key={note} className="inline-flex items-center gap-2 rounded-lg border border-orange-100 bg-orange-50 px-3 py-2 text-sm">
                    <input type="checkbox" name="notes" value={note} />
                    {note}
                  </label>
                ))}
              </div>
            </fieldset>
            <label className="block">
              <span className="text-sm font-semibold text-stone-700">订单备注</span>
              <textarea name="remark" rows={4} className="mt-2 w-full rounded-lg border border-stone-200 px-4 py-3 outline-orange-400" />
            </label>
            {createOrder.isError ? <ErrorState message={createOrder.error.message} /> : null}
          </div>
          <aside className="h-fit rounded-lg border border-orange-100 bg-white p-5 shadow-sm">
            <p className="font-semibold text-stone-950">订单明细</p>
            <div className="mt-4 space-y-3">
              {cart.data.items.map((item) => (
                <div key={item.id} className="flex justify-between gap-4 text-sm">
                  <span className="text-stone-600">
                    {item.dishName} × {item.quantity}
                  </span>
                  <span className="font-semibold">{formatMoney(item.subtotal)}</span>
                </div>
              ))}
            </div>
            <div className="mt-5 border-t border-orange-100 pt-5">
              <div className="flex justify-between">
                <span className="text-stone-500">合计</span>
                <span className="text-2xl font-bold text-orange-600">{formatMoney(cart.data.totalAmount)}</span>
              </div>
              <button
                type="submit"
                disabled={createOrder.isPending}
                className="mt-5 w-full rounded-lg bg-orange-500 px-5 py-3 font-semibold text-white hover:bg-orange-600 disabled:bg-stone-300"
              >
                {createOrder.isPending ? '提交中' : '提交订单'}
              </button>
            </div>
          </aside>
        </form>
      ) : null}
    </div>
  )
}
