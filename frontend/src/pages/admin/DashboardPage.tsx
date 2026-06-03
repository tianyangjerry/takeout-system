import { useQuery } from '@tanstack/react-query'
import ReactECharts from 'echarts-for-react'
import type { EChartsOption } from 'echarts'
import { statisticsApi } from '../../api/statistics'
import { ErrorState, LoadingBlock } from '../../components/PageState'
import { SectionTitle } from '../../components/SectionTitle'
import { StatCard } from '../../components/StatCard'
import { formatCount, formatMoney } from '../../utils/format'

export function DashboardPage() {
  const overview = useQuery({ queryKey: ['admin', 'overview'], queryFn: statisticsApi.overview })
  const trend = useQuery({ queryKey: ['admin', 'sales-trend'], queryFn: () => statisticsApi.salesTrend(7) })
  const top = useQuery({ queryKey: ['admin', 'top-dishes'], queryFn: () => statisticsApi.topDishes(5) })
  const categorySales = useQuery({ queryKey: ['admin', 'category-sales'], queryFn: statisticsApi.categorySales })

  const trendOption: EChartsOption = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: trend.data?.map((item) => item.date) ?? [] },
    yAxis: { type: 'value' },
    series: [{ type: 'line', smooth: true, data: trend.data?.map((item) => item.revenue) ?? [], name: '营业额' }],
  }

  const topOption: EChartsOption = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: top.data?.map((item) => item.dishName) ?? [] },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: top.data?.map((item) => item.sales) ?? [], name: '销量', itemStyle: { color: '#f97316' } }],
  }

  const categoryOption: EChartsOption = {
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['42%', '70%'],
        data: categorySales.data?.map((item) => ({ name: item.categoryName, value: item.revenue })) ?? [],
      },
    ],
  }

  return (
    <div className="space-y-6">
      <SectionTitle title="数据看板" extra="订单、营业额、库存和销售趋势" />
      {overview.isLoading ? <LoadingBlock /> : null}
      {overview.isError ? <ErrorState message={overview.error.message} /> : null}
      {overview.data ? (
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          <StatCard label="今日订单数" value={formatCount(overview.data.todayOrderCount)} />
          <StatCard label="今日营业额" value={formatMoney(overview.data.todayRevenue)} />
          <StatCard label="待处理订单" value={formatCount(overview.data.pendingOrderCount)} />
          <StatCard label="低库存菜品" value={formatCount(overview.data.lowStockDishCount)} />
          <StatCard label="用户总数" value={formatCount(overview.data.totalUserCount)} />
          <StatCard label="菜品总数" value={formatCount(overview.data.totalDishCount)} />
        </div>
      ) : null}
      <div className="grid gap-6 xl:grid-cols-2">
        <ChartPanel title="最近 7 天销售趋势" loading={trend.isLoading} error={trend.isError ? trend.error.message : undefined} option={trendOption} />
        <ChartPanel title="热销菜品 Top 5" loading={top.isLoading} error={top.isError ? top.error.message : undefined} option={topOption} />
        <div className="xl:col-span-2">
          <ChartPanel
            title="分类销售占比"
            loading={categorySales.isLoading}
            error={categorySales.isError ? categorySales.error.message : undefined}
            option={categoryOption}
          />
        </div>
      </div>
    </div>
  )
}

function ChartPanel({ title, loading, error, option }: { title: string; loading: boolean; error?: string; option: EChartsOption }) {
  return (
    <section className="rounded-lg border border-stone-200 bg-white p-5 shadow-sm">
      <h2 className="text-lg font-bold text-stone-950">{title}</h2>
      <div className="mt-4">
        {loading ? <LoadingBlock /> : null}
        {error ? <ErrorState message={error} /> : null}
        {!loading && !error ? <ReactECharts option={option} style={{ height: 320 }} /> : null}
      </div>
    </section>
  )
}
