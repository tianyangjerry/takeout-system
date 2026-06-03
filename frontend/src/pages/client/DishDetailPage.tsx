import { ShoppingCartOutlined } from '@ant-design/icons'
import { useMutation, useQuery } from '@tanstack/react-query'
import { useNavigate, useParams } from 'react-router-dom'
import { cartApi } from '../../api/cart'
import { dishApi } from '../../api/dishes'
import { ErrorState, LoadingBlock } from '../../components/PageState'
import { StockBadge } from '../../components/StatusBadge'
import { useAuthStore } from '../../stores/authStore'
import { resolveImageUrl } from '../../utils/assets'
import { formatMoney } from '../../utils/format'

export function DishDetailPage() {
  const params = useParams()
  const id = Number(params.id)
  const navigate = useNavigate()
  const token = useAuthStore((state) => state.token)
  const dish = useQuery({ queryKey: ['dish', id], queryFn: () => dishApi.detail(id), enabled: Number.isFinite(id) })
  const addCart = useMutation({ mutationFn: cartApi.addItem })

  function handleAdd() {
    if (!dish.data) {
      return
    }
    if (!token) {
      navigate('/login')
      return
    }
    addCart.mutate({ dishId: dish.data.id, quantity: 1 })
  }

  if (dish.isLoading) {
    return <LoadingBlock />
  }

  if (dish.isError) {
    return <ErrorState message={dish.error.message} />
  }

  if (!dish.data) {
    return <ErrorState title="菜品不存在" />
  }

  const imageUrl = resolveImageUrl(dish.data.imageUrl)

  return (
    <div className="grid gap-8 lg:grid-cols-[minmax(0,1fr)_420px]">
      <div className="overflow-hidden rounded-lg border border-orange-100 bg-white shadow-sm">
        <div className="aspect-[16/10] bg-orange-50">
          {imageUrl ? <img src={imageUrl} alt={dish.data.name} className="h-full w-full object-cover" /> : null}
        </div>
      </div>
      <aside className="rounded-lg border border-orange-100 bg-white p-6 shadow-sm">
        <p className="text-sm font-semibold text-orange-700">{dish.data.categoryName || '菜品详情'}</p>
        <h1 className="mt-2 text-3xl font-bold text-stone-950">{dish.data.name}</h1>
        <p className="mt-4 text-3xl font-bold text-orange-600">{formatMoney(dish.data.price)}</p>
        <div className="mt-5 flex flex-wrap gap-3 text-sm text-stone-500">
          <StockBadge stock={dish.data.stock} />
          <span>销量 {dish.data.sales}</span>
          {dish.data.rating ? <span>评分 {dish.data.rating.toFixed(1)}</span> : null}
        </div>
        <p className="mt-6 leading-7 text-stone-600">{dish.data.description || '暂无菜品介绍。'}</p>
        {addCart.isError ? <div className="mt-4"><ErrorState message={addCart.error.message} /></div> : null}
        <button
          type="button"
          disabled={addCart.isPending || dish.data.stock <= 0 || dish.data.status === 0}
          onClick={handleAdd}
          className="mt-8 inline-flex w-full items-center justify-center gap-2 rounded-lg bg-orange-500 px-5 py-3 font-semibold text-white hover:bg-orange-600 disabled:bg-stone-300"
        >
          <ShoppingCartOutlined />
          {addCart.isPending ? '添加中' : '加入购物车'}
        </button>
      </aside>
    </div>
  )
}
