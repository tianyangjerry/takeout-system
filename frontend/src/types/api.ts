export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
  pages: number
}

export interface PageQuery {
  page?: number
  pageSize?: number
}
