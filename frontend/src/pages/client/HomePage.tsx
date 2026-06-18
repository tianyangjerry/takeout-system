import { SearchOutlined } from '@ant-design/icons'
import { useMutation, useQuery } from '@tanstack/react-query'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { cartApi } from '../../api/cart'
import { categoryApi } from '../../api/categories'
import { dishApi } from '../../api/dishes'
import { DishCard } from '../../components/DishCard'
import { EmptyState, ErrorState, LoadingBlock } from '../../components/PageState'
import { SectionTitle } from '../../components/SectionTitle'
import heroImage from '../../assets/hero-campus-takeout.png'
import { useAuthStore } from '../../stores/authStore'
import type { Dish } from '../../types/dish'

export function HomePage() {
  const navigate = useNavigate()
  const token = useAuthStore((state) => state.token)
  const categories = useQuery({ queryKey: ['categories'], queryFn: categoryApi.list })
  const recommend = useQuery({ queryKey: ['dishes', 'recommend'], queryFn: () => dishApi.recommend(6) })
  const top = useQuery({ queryKey: ['dishes', 'top'], queryFn: () => dishApi.top(5) })
  const dishes = useQuery({
    queryKey: ['dishes', 'home'],
    queryFn: () => dishApi.list({ page: 1, pageSize: 8 }),
  })
  const addCart = useMutation({ mutationFn: cartApi.addItem })

  function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const keyword = String(form.get('keyword') || '').trim()
    navigate(keyword ? `/dishes?keyword=${encodeURIComponent(keyword)}` : '/dishes')
  }

  function handleAdd(dish: Dish) {
    if (!token) {
      navigate('/login')
      return
    }
    addCart.mutate({ dishId: dish.id, quantity: 1 })
  }

  return (
    <div className="space-y-10">
      <section
        className="relative overflow-hidden rounded-lg bg-stone-950 px-6 py-16 text-white md:px-12"
        style={{ backgroundImage: `linear-gradient(90deg, rgba(43,33,24,.86), rgba(43,33,24,.32)), url(${heroImage})` }}
      >
        <div className="relative max-w-2xl">
          <h1 className="text-4xl font-bold leading-tight md:text-5xl">智慧校园外卖点餐系统</h1>
          <p className="mt-4 max-w-xl text-white/80">课间、饭点和晚自习后的校园点餐入口。</p>
          <form onSubmit={handleSearch} className="mt-8 flex max-w-xl overflow-hidden rounded-lg bg-white p-1">
            <input name="keyword" placeholder="搜索菜品，例如 黄焖鸡" className="min-w-0 flex-1 px-4 text-stone-900 outline-none" />
            <button type="submit" className="inline-flex items-center gap-2 rounded-lg bg-orange-500 px-5 py-3 font-semibold text-white">
              <SearchOutlined />
              搜索
            </button>
          </form>
        </div>
      </section>

      <section>
        <SectionTitle title="菜品分类" />
        {categories.isLoading ? <LoadingBlock /> : null}
        {categories.isError ? <ErrorState message={categories.error.message} /> : null}
        {categories.data ? (
          <div className="flex flex-wrap gap-3">
            {categories.data.map((category) => (
              <Link
                key={category.id}
                to={`/dishes?categoryId=${category.id}`}
                className="rounded-lg border border-orange-100 bg-white px-5 py-3 font-semibold text-stone-800 shadow-sm hover:border-orange-300 hover:text-orange-700"
              >
                {category.name}
                {typeof category.dishCount === 'number' ? <span className="ml-2 text-sm text-stone-400">{category.dishCount}</span> : null}
              </Link>
            ))}
          </div>
        ) : null}
      </section>

      <section>
        <SectionTitle title="今日推荐" />
        {recommend.isLoading ? <LoadingBlock /> : null}
        {recommend.isError ? <ErrorState message={recommend.error.message} /> : null}
        {recommend.data?.length ? (
          <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
            {recommend.data.map((dish) => (
              <DishCard key={dish.id} dish={dish} onAdd={handleAdd} adding={addCart.isPending} />
            ))}
          </div>
        ) : recommend.data ? (
          <EmptyState />
        ) : null}
      </section>

      <section className="grid gap-6 lg:grid-cols-[1fr_340px]">
        <div>
          <SectionTitle title="菜品列表" extra="更多菜品可进入菜品页筛选" />
          {dishes.isLoading ? <LoadingBlock /> : null}
          {dishes.isError ? <ErrorState message={dishes.error.message} /> : null}
          {dishes.data?.records.length ? (
            <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-4">
              {dishes.data.records.map((dish) => (
                <DishCard key={dish.id} dish={dish} onAdd={handleAdd} adding={addCart.isPending} />
              ))}
            </div>
          ) : dishes.data ? (
            <EmptyState />
          ) : null}
        </div>
        <aside>
          <SectionTitle title="热销榜" />
          {top.isLoading ? <LoadingBlock /> : null}
          {top.isError ? <ErrorState message={top.error.message} /> : null}
          {top.data?.length ? (
            <div className="rounded-lg border border-orange-100 bg-white p-4 shadow-sm">
              {top.data.map((dish, index) => (
                <Link key={dish.id} to={`/dishes/${dish.id}`} className="flex items-center gap-3 border-b border-orange-50 py-3 last:border-0">
                  <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-orange-100 font-bold text-orange-700">{index + 1}</span>
                  <span className="min-w-0 flex-1 truncate font-semibold text-stone-800">{dish.name}</span>
                  <span className="text-sm text-stone-500">{dish.sales}</span>
                </Link>
              ))}
            </div>
          ) : top.data ? (
            <EmptyState />
          ) : null}
        </aside>
      </section>
    </div>
  )
}
