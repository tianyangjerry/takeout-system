import { useMutation } from '@tanstack/react-query'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../../api/auth'
import { ErrorState } from '../../components/PageState'
import { useAuthStore } from '../../stores/authStore'

export function LoginPage() {
  const navigate = useNavigate()
  const setSession = useAuthStore((state) => state.setSession)
  const mutation = useMutation({
    mutationFn: authApi.login,
    onSuccess: (data) => {
      setSession(data.token, data.user)
      navigate(data.user.role === 'ADMIN' ? '/admin/dashboard' : '/')
    },
  })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    mutation.mutate({
      username: String(form.get('username') || ''),
      password: String(form.get('password') || ''),
    })
  }

  return (
    <div className="rounded-lg border border-orange-100 bg-white p-8 shadow-sm">
      <h1 className="text-2xl font-bold text-stone-950">用户登录</h1>
      <form className="mt-6 space-y-4" onSubmit={handleSubmit}>
        <label className="block">
          <span className="text-sm font-semibold text-stone-700">用户名</span>
          <input name="username" required className="mt-2 w-full rounded-lg border border-stone-200 px-4 py-3 outline-orange-400" />
        </label>
        <label className="block">
          <span className="text-sm font-semibold text-stone-700">密码</span>
          <input
            name="password"
            type="password"
            required
            minLength={6}
            className="mt-2 w-full rounded-lg border border-stone-200 px-4 py-3 outline-orange-400"
          />
        </label>
        {mutation.isError ? <ErrorState message={mutation.error.message} /> : null}
        <button
          type="submit"
          disabled={mutation.isPending}
          className="w-full rounded-lg bg-orange-500 px-5 py-3 font-semibold text-white hover:bg-orange-600 disabled:bg-stone-300"
        >
          {mutation.isPending ? '登录中' : '登录'}
        </button>
      </form>
      <p className="mt-5 text-center text-sm text-stone-500">
        没有账号？
        <Link to="/register" className="font-semibold text-orange-700">
          去注册
        </Link>
      </p>
    </div>
  )
}
