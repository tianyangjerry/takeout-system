import type { PageQuery } from './api'

export type DishStatus = 0 | 1
export type DishQueryStatus = -1 | DishStatus
export type DishSort = 'sales' | 'price_asc' | 'price_desc' | 'rating'

export interface Dish {
  id: number
  categoryId: number
  categoryName?: string
  name: string
  price: number
  stock: number
  imageUrl?: string
  description?: string
  status: DishStatus
  sales: number
  rating?: number
  recommendScore?: number
  createdTime?: string
  updatedTime?: string
}

export interface DishQuery extends PageQuery {
  keyword?: string
  categoryId?: number
  status?: DishQueryStatus
  sort?: DishSort
}

export interface DishPayload {
  categoryId: number
  name: string
  price: number
  stock: number
  imageUrl?: string
  description?: string
  status: DishStatus
}

export interface UploadImageResponse {
  url: string
  fileName: string
}
