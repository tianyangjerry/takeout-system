import { useQuery } from '@tanstack/react-query'
import { statisticsApi } from '../../api/statistics'
import { EmptyState, ErrorState, LoadingBlock } from '../../components/PageState'
import { SectionTitle } from '../../components/SectionTitle'

export function StockPage() {
  const stock = useQuery({ queryKey: ['admin', 'stock-alert'], queryFn: () => statisticsApi.stockAlert(10) })

  return (
    <div className="space-y-6">
      <SectionTitle title="库存预警" extra="默认阈值：库存低于 10" />
      {stock.isLoading ? <LoadingBlock /> : null}
      {stock.isError ? <ErrorState message={stock.error.message} /> : null}
      {stock.data?.length ? (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {stock.data.map((item) => (
            <article key={item.dishId} className="rounded-lg border border-orange-100 bg-white p-5 shadow-sm">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="font-semibold text-stone-950">{item.dishName}</p>
                  <p className="mt-2 text-sm text-stone-500">预警阈值 {item.threshold}</p>
                </div>
                <span className={`rounded-full px-3 py-1 text-xs font-semibold ${item.status === 'SOLD_OUT' ? 'bg-red-100 text-red-700' : 'bg-amber-100 text-amber-700'}`}>
                  {item.statusText}
                </span>
              </div>
              <p className="mt-5 text-3xl font-bold text-orange-600">{item.stock}</p>
            </article>
          ))}
        </div>
      ) : stock.data ? (
        <EmptyState title="暂无库存预警" message="所有菜品库存都处于安全范围。" />
      ) : null}
    </div>
  )
}
