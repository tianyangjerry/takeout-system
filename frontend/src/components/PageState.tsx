import { Link } from 'react-router-dom'

interface StateProps {
  title?: string
  message?: string
}

export function LoadingBlock({ message = '正在加载数据...' }: StateProps) {
  return (
    <div className="rounded-lg border border-orange-100 bg-white p-8 text-center text-sm text-stone-500 shadow-sm">
      {message}
    </div>
  )
}

export function ErrorState({ title = '数据暂时不可用', message = '请确认后端服务已启动。' }: StateProps) {
  return (
    <div className="rounded-lg border border-red-100 bg-red-50 p-6 text-sm text-red-700">
      <p className="font-semibold">{title}</p>
      <p className="mt-2 text-red-600">{message}</p>
    </div>
  )
}

export function EmptyState({ title = '暂无数据', message = '稍后再来看看。' }: StateProps) {
  return (
    <div className="rounded-lg border border-dashed border-stone-300 bg-white/70 p-8 text-center">
      <p className="font-semibold text-stone-800">{title}</p>
      <p className="mt-2 text-sm text-stone-500">{message}</p>
    </div>
  )
}

export function LoginRequired() {
  return (
    <div className="mx-auto max-w-xl rounded-lg border border-orange-100 bg-white p-8 text-center shadow-sm">
      <p className="text-xl font-semibold text-stone-900">请先登录</p>
      <p className="mt-2 text-sm text-stone-500">登录后可以继续访问购物车、订单和后台页面。</p>
      <Link
        to="/login"
        className="mt-6 inline-flex rounded-lg bg-orange-500 px-5 py-2.5 text-sm font-semibold text-white hover:bg-orange-600"
      >
        去登录
      </Link>
    </div>
  )
}
