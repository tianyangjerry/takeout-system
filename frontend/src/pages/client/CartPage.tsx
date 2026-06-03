import { DeleteOutlined, MinusOutlined, PlusOutlined } from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { cartApi } from '../../api/cart'
import { EmptyState, ErrorState, LoadingBlock, LoginRequired } from '../../components/PageState'
import { useAuthStore } from '../../stores/authStore'
import { resolveImageUrl } from '../../utils/assets'
import { formatMoney } from '../../utils/format'

export function CartPage() {
  const token = useAuthStore((state) => state.token)
  const queryClient = useQueryClient()
  const cart = useQuery({ queryKey: ['cart'], queryFn: cartApi.get, enabled: Boolean(token) })
  const refresh = () => void queryClient.invalidateQueries({ queryKey: ['cart'] })
  const updateItem = useMutation({ mutationFn: ({ id, quantity }: { id: number; quantity: number }) => cartApi.updateItem(id, { quantity }), onSuccess: refresh })
  const removeItem = useMutation({ mutationFn: cartApi.removeItem, onSuccess: refresh })
  const clearCart = useMutation({ mutationFn: cartApi.clear, onSuccess: refresh })

  if (!token) {
    return <LoginRequired />
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 className="text-3xl font-bold text-stone-950">购物车</h1>
          <p className="mt-2 text-stone-500">确认菜品数量后提交订单。</p>
        </div>
        <button
          type="button"
          disabled={clearCart.isPending || !cart.data?.items.length}
          onClick={() => clearCart.mutate()}
          className="rounded-lg border border-stone-200 bg-white px-4 py-2 text-sm font-semibold text-stone-700 disabled:text-stone-300"
        >
          清空购物车
        </button>
      </div>
      {cart.isLoading ? <LoadingBlock /> : null}
      {cart.isError ? <ErrorState message={cart.error.message} /> : null}
      {updateItem.isError ? <ErrorState message={updateItem.error.message} /> : null}
      {removeItem.isError ? <ErrorState message={removeItem.error.message} /> : null}
      {clearCart.isError ? <ErrorState message={clearCart.error.message} /> : null}
      {cart.data?.items.length ? (
        <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
          <div className="space-y-3">
            {cart.data.items.map((item) => {
              const imageUrl = resolveImageUrl(item.imageUrl)
              return (
                <div key={item.id} className="grid gap-4 rounded-lg border border-orange-100 bg-white p-4 shadow-sm sm:grid-cols-[96px_1fr_auto]">
                  <div className="h-24 overflow-hidden rounded-lg bg-orange-50">
                    {imageUrl ? <img src={imageUrl} alt={item.dishName} className="h-full w-full object-cover" /> : null}
                  </div>
                  <div>
                    <p className="font-semibold text-stone-950">{item.dishName}</p>
                    <p className="mt-1 text-sm text-stone-500">{formatMoney(item.price)} / 份</p>
                    <p className="mt-2 text-sm text-stone-500">库存 {item.stock}</p>
                  </div>
                  <div className="flex items-center gap-3">
                    <button
                      type="button"
                      title="减少数量"
                      disabled={item.quantity <= 1 || updateItem.isPending}
                      onClick={() => updateItem.mutate({ id: item.id, quantity: item.quantity - 1 })}
                      className="h-9 w-9 rounded-lg border border-stone-200 bg-white disabled:text-stone-300"
                    >
                      <MinusOutlined />
                    </button>
                    <span className="w-8 text-center font-semibold">{item.quantity}</span>
                    <button
                      type="button"
                      title="增加数量"
                      disabled={item.quantity >= item.stock || updateItem.isPending}
                      onClick={() => updateItem.mutate({ id: item.id, quantity: item.quantity + 1 })}
                      className="h-9 w-9 rounded-lg border border-stone-200 bg-white disabled:text-stone-300"
                    >
                      <PlusOutlined />
                    </button>
                    <button
                      type="button"
                      title="删除"
                      disabled={removeItem.isPending}
                      onClick={() => removeItem.mutate(item.id)}
                      className="h-9 w-9 rounded-lg border border-red-100 bg-red-50 text-red-600"
                    >
                      <DeleteOutlined />
                    </button>
                  </div>
                </div>
              )
            })}
          </div>
          <aside className="h-fit rounded-lg border border-orange-100 bg-white p-5 shadow-sm">
            <p className="text-sm text-stone-500">合计数量</p>
            <p className="mt-1 text-2xl font-bold text-stone-950">{cart.data.totalQuantity}</p>
            <p className="mt-5 text-sm text-stone-500">订单金额</p>
            <p className="mt-1 text-3xl font-bold text-orange-600">{formatMoney(cart.data.totalAmount)}</p>
            <Link to="/checkout" className="mt-6 block rounded-lg bg-orange-500 px-5 py-3 text-center font-semibold text-white hover:bg-orange-600">
              去结算
            </Link>
          </aside>
        </div>
      ) : cart.data ? (
        <EmptyState title="购物车为空" message="去菜品页添加喜欢的菜品。" />
      ) : null}
    </div>
  )
}
