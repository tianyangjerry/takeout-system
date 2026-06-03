import { SearchOutlined } from '@ant-design/icons'
import { useMutation, useQuery } from '@tanstack/react-query'
import type { FormEvent } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { cartApi } from '../../api/cart'
import { categoryApi } from '../../api/categories'
import { dishApi } from '../../api/dishes'
import { DishCard } from '../../components/DishCard'
import { EmptyState, ErrorState, LoadingBlock } from '../../components/PageState'
import { useAuthStore } from '../../stores/authStore'
import type { Dish, DishSort } from '../../types/dish'

export function DishListPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const navigate = useNavigate()
  const token = useAuthStore((state) => state.token)
  const page = Number(searchParams.get('page') || 1)
  const keyword = searchParams.get('keyword') || ''
  const categoryId = searchParams.get('categoryId')
  const sort = searchParams.get('sort') as DishSort | null
  const categories = useQuery({ queryKey: ['categories'], queryFn: categoryApi.list })
  const dishes = useQuery({
    queryKey: ['dishes', { keyword, categoryId, sort, page }],
    queryFn: () =>
      dishApi.list({
        keyword: keyword || undefined,
        categoryId: categoryId ? Number(categoryId) : undefined,
        sort: sort || undefined,
        page,
        pageSize: 12,
      }),
  })
  const addCart = useMutation({ mutationFn: cartApi.addItem })

  function updateParam(key: string, value?: string) {
    const next = new URLSearchParams(searchParams)
    if (value) {
      next.set(key, value)
    } else {
      next.delete(key)
    }
    next.set('page', '1')
    setSearchParams(next)
  }

  function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    updateParam('keyword', String(form.get('keyword') || '').trim())
  }

  function handleAdd(dish: Dish) {
    if (!token) {
      navigate('/login')
      return
    }
    addCart.mutate({ dishId: dish.id, quantity: 1 })
  }

  function setPage(nextPage: number) {
    const next = new URLSearchParams(searchParams)
    next.set('page', String(nextPage))
    setSearchParams(next)
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-stone-950">菜品列表</h1>
        <p className="mt-2 text-stone-500">按分类、关键词和排序查找菜品。</p>
      </div>
      <div className="rounded-lg border border-orange-100 bg-white p-4 shadow-sm">
        <form onSubmit={handleSearch} className="grid gap-3 md:grid-cols-[1fr_180px_180px_auto]">
          <input
            name="keyword"
            defaultValue={keyword}
            placeholder="输入菜品名称"
            className="rounded-lg border border-stone-200 px-4 py-2.5 outline-orange-400"
          />
          <select
            value={categoryId || ''}
            onChange={(event) => updateParam('categoryId', event.target.value)}
            className="rounded-lg border border-stone-200 px-4 py-2.5 outline-orange-400"
          >
            <option value="">全部分类</option>
            {categories.data?.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
          <select
            value={sort || ''}
            onChange={(event) => updateParam('sort', event.target.value)}
            className="rounded-lg border border-stone-200 px-4 py-2.5 outline-orange-400"
          >
            <option value="">默认排序</option>
            <option value="sales">销量优先</option>
            <option value="price_asc">价格从低到高</option>
            <option value="price_desc">价格从高到低</option>
            <option value="rating">评分优先</option>
          </select>
          <button type="submit" className="inline-flex items-center justify-center gap-2 rounded-lg bg-orange-500 px-5 py-2.5 font-semibold text-white">
            <SearchOutlined />
            搜索
          </button>
        </form>
      </div>

      {dishes.isLoading ? <LoadingBlock /> : null}
      {dishes.isError ? <ErrorState message={dishes.error.message} /> : null}
      {addCart.isError ? <ErrorState message={addCart.error.message} /> : null}
      {dishes.data?.records.length ? (
        <>
          <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {dishes.data.records.map((dish) => (
              <DishCard key={dish.id} dish={dish} onAdd={handleAdd} adding={addCart.isPending} />
            ))}
          </div>
          <div className="flex items-center justify-center gap-3">
            <button
              type="button"
              disabled={page <= 1}
              onClick={() => setPage(page - 1)}
              className="rounded-lg border border-stone-200 bg-white px-4 py-2 disabled:text-stone-300"
            >
              上一页
            </button>
            <span className="text-sm text-stone-500">
              {page} / {dishes.data.pages}
            </span>
            <button
              type="button"
              disabled={page >= dishes.data.pages}
              onClick={() => setPage(page + 1)}
              className="rounded-lg border border-stone-200 bg-white px-4 py-2 disabled:text-stone-300"
            >
              下一页
            </button>
          </div>
        </>
      ) : dishes.data ? (
        <EmptyState title="没有找到菜品" message="换个关键词或分类试试。" />
      ) : null}
    </div>
  )
}
