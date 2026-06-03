export interface CartItem {
  id: number
  dishId: number
  dishName: string
  price: number
  quantity: number
  stock: number
  imageUrl?: string
  subtotal: number
}

export interface Cart {
  items: CartItem[]
  totalAmount: number
  totalQuantity: number
}

export interface AddCartItemRequest {
  dishId: number
  quantity: number
}

export interface UpdateCartItemRequest {
  quantity: number
}
