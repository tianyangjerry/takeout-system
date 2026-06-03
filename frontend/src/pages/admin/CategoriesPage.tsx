import { DeleteOutlined, EditOutlined } from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { FormEvent } from 'react'
import { useState } from 'react'
import { categoryApi } from '../../api/categories'
import { EmptyState, ErrorState, LoadingBlock } from '../../components/PageState'
import { SectionTitle } from '../../components/SectionTitle'
import type { Category } from '../../types/category'

export function CategoriesPage() {
  const queryClient = useQueryClient()
  const [editing, setEditing] = useState<Category | null>(null)
  const categories = useQuery({ queryKey: ['categories'], queryFn: categoryApi.list })
  const refresh = () => void queryClient.invalidateQueries({ queryKey: ['categories'] })
  const create = useMutation({ mutationFn: categoryApi.create, onSuccess: refresh })
  const update = useMutation({
    mutationFn: ({ id, name, sortOrder }: { id: number; name: string; sortOrder: number }) => categoryApi.update(id, { name, sortOrder }),
    onSuccess: () => {
      setEditing(null)
      refresh()
    },
  })
  const remove = useMutation({ mutationFn: categoryApi.remove, onSuccess: refresh })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const payload = {
      name: String(form.get('name') || ''),
      sortOrder: Number(form.get('sortOrder') || 0),
    }
    if (editing) {
      update.mutate({ id: editing.id, ...payload })
    } else {
      create.mutate(payload)
    }
    event.currentTarget.reset()
  }

  const actionError = create.error?.message || update.error?.message || remove.error?.message

  return (
    <div className="space-y-6">
      <SectionTitle title="分类管理" extra="分类下已有菜品时后端会阻止删除" />
      <form onSubmit={handleSubmit} className="grid gap-3 rounded-lg border border-stone-200 bg-white p-4 shadow-sm md:grid-cols-[1fr_160px_auto]">
        <input
          name="name"
          required
          defaultValue={editing?.name}
          placeholder="分类名称"
          className="rounded-lg border border-stone-200 px-4 py-2.5 outline-orange-400"
        />
        <input
          name="sortOrder"
          type="number"
          defaultValue={editing?.sortOrder ?? 0}
          className="rounded-lg border border-stone-200 px-4 py-2.5 outline-orange-400"
        />
        <button type="submit" className="rounded-lg bg-orange-500 px-5 py-2.5 font-semibold text-white">
          {editing ? '保存修改' : '新增分类'}
        </button>
      </form>
      {actionError ? <ErrorState message={actionError} /> : null}
      {categories.isLoading ? <LoadingBlock /> : null}
      {categories.isError ? <ErrorState message={categories.error.message} /> : null}
      {categories.data?.length ? (
        <div className="overflow-hidden rounded-lg border border-stone-200 bg-white shadow-sm">
          <table className="w-full min-w-[680px] text-left text-sm">
            <thead className="bg-stone-50 text-stone-500">
              <tr>
                <th className="px-4 py-3">ID</th>
                <th className="px-4 py-3">名称</th>
                <th className="px-4 py-3">排序</th>
                <th className="px-4 py-3">菜品数</th>
                <th className="px-4 py-3">操作</th>
              </tr>
            </thead>
            <tbody>
              {categories.data.map((category) => (
                <tr key={category.id} className="border-t border-stone-100">
                  <td className="px-4 py-3">{category.id}</td>
                  <td className="px-4 py-3 font-semibold">{category.name}</td>
                  <td className="px-4 py-3">{category.sortOrder}</td>
                  <td className="px-4 py-3">{category.dishCount ?? '-'}</td>
                  <td className="px-4 py-3">
                    <div className="flex gap-2">
                      <button type="button" title="编辑" onClick={() => setEditing(category)} className="h-9 w-9 rounded-lg bg-orange-50 text-orange-700">
                        <EditOutlined />
                      </button>
                      <button type="button" title="删除" onClick={() => remove.mutate(category.id)} className="h-9 w-9 rounded-lg bg-red-50 text-red-600">
                        <DeleteOutlined />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : categories.data ? (
        <EmptyState />
      ) : null}
    </div>
  )
}
