import { ShoppingCartOutlined } from '@ant-design/icons'
import { Link } from 'react-router-dom'
import type { Dish } from '../types/dish'
import { resolveImageUrl } from '../utils/assets'
import { formatMoney } from '../utils/format'
import { StockBadge } from './StatusBadge'

interface DishCardProps {
  dish: Dish
  onAdd?: (dish: Dish) => void
  adding?: boolean
}

export function DishCard({ dish, onAdd, adding = false }: DishCardProps) {
  const imageUrl = resolveImageUrl(dish.imageUrl)

  return (
    <article className="overflow-hidden rounded-lg border border-orange-100 bg-white text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
      <Link to={`/dishes/${dish.id}`} className="block">
        <div className="aspect-[4/3] bg-orange-50">
          {imageUrl ? (
            <img src={imageUrl} alt={dish.name} className="h-full w-full object-cover" />
          ) : (
            <div className="flex h-full items-center justify-center text-sm text-orange-300">暂无图片</div>
          )}
        </div>
      </Link>
      <div className="space-y-4 p-4">
        <div>
          <div className="flex items-start justify-between gap-3">
            <Link to={`/dishes/${dish.id}`} className="font-semibold text-stone-950 hover:text-orange-600">
              {dish.name}
            </Link>
            <span className="font-bold text-orange-600">{formatMoney(dish.price)}</span>
          </div>
          <p className="mt-1 line-clamp-2 text-sm text-stone-500">{dish.description || dish.categoryName || '校园热卖菜品'}</p>
        </div>
        <div className="flex flex-wrap items-center gap-2 text-xs text-stone-500">
          <StockBadge stock={dish.stock} />
          <span>销量 {dish.sales}</span>
          {dish.rating ? <span>评分 {dish.rating.toFixed(1)}</span> : null}
        </div>
        <button
          type="button"
          disabled={adding || dish.stock <= 0 || dish.status === 0}
          onClick={() => onAdd?.(dish)}
          className="inline-flex w-full items-center justify-center gap-2 rounded-lg bg-orange-500 px-4 py-2.5 text-sm font-semibold text-white hover:bg-orange-600 disabled:bg-stone-300"
          title="加入购物车"
        >
          <ShoppingCartOutlined />
          {adding ? '添加中' : '加入购物车'}
        </button>
      </div>
    </article>
  )
}
