import { Navigate, Route, Routes } from 'react-router-dom'
import { AdminLayout } from '../layouts/AdminLayout'
import { AuthLayout } from '../layouts/AuthLayout'
import { ClientLayout } from '../layouts/ClientLayout'
import { AdminOrdersPage } from '../pages/admin/AdminOrdersPage'
import { CategoriesPage } from '../pages/admin/CategoriesPage'
import { DashboardPage } from '../pages/admin/DashboardPage'
import { DishesPage } from '../pages/admin/DishesPage'
import { StockPage } from '../pages/admin/StockPage'
import { LoginPage } from '../pages/auth/LoginPage'
import { RegisterPage } from '../pages/auth/RegisterPage'
import { CartPage } from '../pages/client/CartPage'
import { CheckoutPage } from '../pages/client/CheckoutPage'
import { DishDetailPage } from '../pages/client/DishDetailPage'
import { DishListPage } from '../pages/client/DishListPage'
import { HomePage } from '../pages/client/HomePage'
import { OrderDetailPage } from '../pages/client/OrderDetailPage'
import { OrdersPage } from '../pages/client/OrdersPage'

export function AppRouter() {
  return (
    <Routes>
      <Route element={<ClientLayout />}>
        <Route index element={<HomePage />} />
        <Route path="dishes" element={<DishListPage />} />
        <Route path="dishes/:id" element={<DishDetailPage />} />
        <Route path="cart" element={<CartPage />} />
        <Route path="checkout" element={<CheckoutPage />} />
        <Route path="orders" element={<OrdersPage />} />
        <Route path="orders/:id" element={<OrderDetailPage />} />
      </Route>
      <Route element={<AuthLayout />}>
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
      </Route>
      <Route path="admin" element={<AdminLayout />}>
        <Route index element={<Navigate to="/admin/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="categories" element={<CategoriesPage />} />
        <Route path="dishes" element={<DishesPage />} />
        <Route path="orders" element={<AdminOrdersPage />} />
        <Route path="orders/:id" element={<OrderDetailPage />} />
        <Route path="stock" element={<StockPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
