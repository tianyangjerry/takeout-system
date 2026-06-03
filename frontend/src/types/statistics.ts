export interface OverviewStats {
  todayOrderCount: number
  todayRevenue: number
  pendingOrderCount: number
  lowStockDishCount: number
  totalUserCount: number
  totalDishCount: number
}

export interface SalesTrendItem {
  date: string
  orderCount: number
  revenue: number
}

export interface TopDishStats {
  dishId: number
  dishName: string
  sales: number
  revenue: number
}

export interface CategorySalesStats {
  categoryId: number
  categoryName: string
  sales: number
  revenue: number
  percent: number
}

export interface StockAlertItem {
  dishId: number
  dishName: string
  stock: number
  threshold: number
  status: 'LOW_STOCK' | 'SOLD_OUT'
  statusText: string
}
