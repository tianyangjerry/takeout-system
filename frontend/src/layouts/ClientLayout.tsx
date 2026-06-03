import { HomeOutlined, LogoutOutlined, ShoppingCartOutlined, UserOutlined } from '@ant-design/icons'
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../stores/authStore'

const navItems = [
  { to: '/', label: '首页' },
  { to: '/dishes', label: '菜品' },
  { to: '/orders', label: '订单' },
]

export function ClientLayout() {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-20 border-b border-orange-100 bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-4 px-4 py-4">
          <Link to="/" className="inline-flex items-center gap-2 text-lg font-bold text-stone-950">
            <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-orange-500 text-white">
              <HomeOutlined />
            </span>
            智慧校园外卖
          </Link>
          <nav className="flex items-center gap-2">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `rounded-lg px-3 py-2 text-sm font-semibold ${
                    isActive ? 'bg-orange-100 text-orange-700' : 'text-stone-600 hover:bg-orange-50'
                  }`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
          <div className="flex items-center gap-2">
            <Link
              to="/cart"
              className="inline-flex h-10 w-10 items-center justify-center rounded-lg border border-orange-100 bg-white text-orange-700 hover:bg-orange-50"
              title="购物车"
            >
              <ShoppingCartOutlined />
            </Link>
            {user?.role === 'ADMIN' ? (
              <Link to="/admin/dashboard" className="rounded-lg bg-stone-900 px-4 py-2 text-sm font-semibold text-white">
                后台
              </Link>
            ) : null}
            {user ? (
              <button
                type="button"
                onClick={handleLogout}
                className="inline-flex items-center gap-2 rounded-lg border border-stone-200 bg-white px-4 py-2 text-sm font-semibold text-stone-700 hover:bg-stone-50"
                title="退出登录"
              >
                <LogoutOutlined />
                退出
              </button>
            ) : (
              <Link
                to="/login"
                className="inline-flex items-center gap-2 rounded-lg bg-orange-500 px-4 py-2 text-sm font-semibold text-white hover:bg-orange-600"
              >
                <UserOutlined />
                登录
              </Link>
            )}
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-7xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}
