import { Link, Outlet } from 'react-router-dom'
import heroImage from '../assets/hero-campus-takeout.png'

export function AuthLayout() {
  return (
    <div className="grid min-h-screen bg-[#fff8ee] lg:grid-cols-[1fr_480px]">
      <section className="hidden bg-cover bg-center lg:block" style={{ backgroundImage: `url(${heroImage})` }}>
        <div className="flex h-full items-end bg-stone-950/35 p-12 text-white">
          <div>
            <p className="text-4xl font-bold">智慧校园外卖点餐系统</p>
            <p className="mt-4 max-w-xl text-white/80">校园食堂点餐、购物车、订单状态和后台管理的一体化体验。</p>
          </div>
        </div>
      </section>
      <section className="flex items-center justify-center px-4 py-10">
        <div className="w-full max-w-md">
          <Link to="/" className="mb-8 inline-flex text-sm font-semibold text-orange-700">
            返回首页
          </Link>
          <Outlet />
        </div>
      </section>
    </div>
  )
}
