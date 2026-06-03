import { apiGet } from './request'
import type {
  CategorySalesStats,
  OverviewStats,
  SalesTrendItem,
  StockAlertItem,
  TopDishStats,
} from '../types/statistics'

export const statisticsApi = {
  overview: () => apiGet<OverviewStats>('/admin/statistics/overview'),
  salesTrend: (days = 7) => apiGet<SalesTrendItem[]>('/admin/statistics/sales-trend', { params: { days } }),
  topDishes: (limit = 5) => apiGet<TopDishStats[]>('/admin/statistics/top-dishes', { params: { limit } }),
  categorySales: () => apiGet<CategorySalesStats[]>('/admin/statistics/category-sales'),
  stockAlert: (threshold = 10) =>
    apiGet<StockAlertItem[]>('/admin/statistics/stock-alert', { params: { threshold } }),
}
