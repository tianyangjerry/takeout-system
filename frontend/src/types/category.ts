export interface Category {
  id: number
  name: string
  sortOrder: number
  dishCount?: number
}

export interface CategoryPayload {
  name: string
  sortOrder: number
}
