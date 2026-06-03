import { DashboardOutlined, HomeOutlined, LogoutOutlined } from '@ant-design/icons'
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import { LoginRequired } from '../components/PageState'
import { useAuthStore } from '../stores/authStore'

const adminNavItems = [
  { to: '/admin/dashboard', label: '数据看板' },
  { to: '/admin/categories', label: '分类管理' },
  { to: '/admin/dishes', label: '菜品管理' },
  { to: '/admin/orders', label: '订单管理' },
  { to: '/admin/stock', label: '库存预警' },
]

export function AdminLayout() {
  const { user, token, logout } = useAuthStore()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  if (!token || user?.role !== 'ADMIN') {
    return (
      <div className="min-h-screen bg-stone-50 px-4 py-16">
        <LoginRequired />
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-stone-50">
      <aside className="fixed inset-y-0 left-0 hidden w-64 border-r border-stone-200 bg-white p-5 lg:block">
        <Link to="/admin/dashboard" className="inline-flex items-center gap-2 text-lg font-bold text-stone-950">
          <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-stone-900 text-white">
            <DashboardOutlined />
          </span>
          管理后台
        </Link>
        <nav className="mt-8 space-y-2">
          {adminNavItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `block rounded-lg px-4 py-3 text-sm font-semibold ${
                  isActive ? 'bg-orange-100 text-orange-700' : 'text-stone-600 hover:bg-stone-100'
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="absolute bottom-5 left-5 right-5 grid gap-2">
          <Link to="/" className="inline-flex items-center justify-center gap-2 rounded-lg border border-stone-200 px-4 py-2 text-sm">
            <HomeOutlined />
            返回前台
          </Link>
          <button
            type="button"
            onClick={handleLogout}
            className="inline-flex items-center justify-center gap-2 rounded-lg bg-stone-900 px-4 py-2 text-sm font-semibold text-white"
          >
            <LogoutOutlined />
            退出
          </button>
        </div>
      </aside>
      <main className="lg:pl-64">
        <div className="mx-auto max-w-7xl px-4 py-8">
          <div className="mb-6 flex flex-wrap gap-2 lg:hidden">
            {adminNavItems.map((item) => (
              <NavLink key={item.to} to={item.to} className="rounded-lg bg-white px-3 py-2 text-sm text-stone-700">
                {item.label}
              </NavLink>
            ))}
          </div>
          <Outlet />
        </div>
      </main>
    </div>
  )
}
