import { DeleteOutlined, EditOutlined } from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { FormEvent } from 'react'
import { useState } from 'react'
import { categoryApi } from '../../api/categories'
import { dishApi } from '../../api/dishes'
import { ImageUploadButton } from '../../components/ImageUploadButton'
import { EmptyState, ErrorState, LoadingBlock } from '../../components/PageState'
import { SectionTitle } from '../../components/SectionTitle'
import { DishStatusBadge, StockBadge } from '../../components/StatusBadge'
import type { Dish, DishPayload } from '../../types/dish'
import { formatMoney } from '../../utils/format'

export function DishesPage() {
  const queryClient = useQueryClient()
  const [editing, setEditing] = useState<Dish | null>(null)
  const [imageUrl, setImageUrl] = useState('')
  const categories = useQuery({ queryKey: ['categories'], queryFn: categoryApi.list })
  const dishes = useQuery({ queryKey: ['admin', 'dishes'], queryFn: () => dishApi.list({ page: 1, pageSize: 50, status: -1 }) })
  const refresh = () => void queryClient.invalidateQueries({ queryKey: ['admin', 'dishes'] })
  const create = useMutation({ mutationFn: dishApi.create, onSuccess: refresh })
  const update = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: DishPayload }) => dishApi.update(id, payload),
    onSuccess: () => {
      setEditing(null)
      setImageUrl('')
      refresh()
    },
  })
  const status = useMutation({ mutationFn: ({ id, next }: { id: number; next: DishPayload['status'] }) => dishApi.updateStatus(id, next), onSuccess: refresh })
  const remove = useMutation({ mutationFn: dishApi.remove, onSuccess: refresh })
  const upload = useMutation({ mutationFn: dishApi.uploadImage, onSuccess: (data) => setImageUrl(data.url) })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const payload: DishPayload = {
      categoryId: Number(form.get('categoryId') || 0),
      name: String(form.get('name') || ''),
      price: Number(form.get('price') || 0),
      stock: Number(form.get('stock') || 0),
      imageUrl: imageUrl || String(form.get('imageUrl') || ''),
      description: String(form.get('description') || ''),
      status: Number(form.get('status') || 1) as DishPayload['status'],
    }
    if (editing) {
      update.mutate({ id: editing.id, payload })
    } else {
      create.mutate(payload)
    }
    event.currentTarget.reset()
  }

  function startEdit(dish: Dish) {
    setEditing(dish)
    setImageUrl(dish.imageUrl || '')
  }

  const actionError = create.error?.message || update.error?.message || status.error?.message || remove.error?.message || upload.error?.message

  return (
    <div className="space-y-6">
      <SectionTitle title="菜品管理" extra="已有订单菜品删除失败时改为下架" />
      <form onSubmit={handleSubmit} className="grid gap-3 rounded-lg border border-stone-200 bg-white p-4 shadow-sm md:grid-cols-2 xl:grid-cols-4">
        <input name="name" required defaultValue={editing?.name} placeholder="菜品名称" className="rounded-lg border border-stone-200 px-4 py-2.5" />
        <select name="categoryId" required defaultValue={editing?.categoryId} className="rounded-lg border border-stone-200 px-4 py-2.5">
          <option value="">选择分类</option>
          {categories.data?.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>
        <input name="price" type="number" step="0.01" required defaultValue={editing?.price} placeholder="价格" className="rounded-lg border border-stone-200 px-4 py-2.5" />
        <input name="stock" type="number" required defaultValue={editing?.stock} placeholder="库存" className="rounded-lg border border-stone-200 px-4 py-2.5" />
        <input name="imageUrl" value={imageUrl} onChange={(event) => setImageUrl(event.target.value)} placeholder="图片地址" className="rounded-lg border border-stone-200 px-4 py-2.5" />
        <select name="status" defaultValue={editing?.status ?? 1} className="rounded-lg border border-stone-200 px-4 py-2.5">
          <option value={1}>上架</option>
          <option value={0}>下架</option>
        </select>
        <div className="flex items-center">
          <ImageUploadButton uploading={upload.isPending} onSelect={(file) => upload.mutate(file)} />
        </div>
        <button type="submit" className="rounded-lg bg-orange-500 px-5 py-2.5 font-semibold text-white">
          {editing ? '保存菜品' : '新增菜品'}
        </button>
        <textarea name="description" defaultValue={editing?.description} placeholder="菜品介绍" className="md:col-span-2 xl:col-span-4 rounded-lg border border-stone-200 px-4 py-2.5" />
      </form>
      {actionError ? <ErrorState message={actionError} /> : null}
      {dishes.isLoading ? <LoadingBlock /> : null}
      {dishes.isError ? <ErrorState message={dishes.error.message} /> : null}
      {dishes.data?.records.length ? (
        <div className="overflow-x-auto rounded-lg border border-stone-200 bg-white shadow-sm">
          <table className="w-full min-w-[960px] text-left text-sm">
            <thead className="bg-stone-50 text-stone-500">
              <tr>
                <th className="px-4 py-3">菜品</th>
                <th className="px-4 py-3">分类</th>
                <th className="px-4 py-3">价格</th>
                <th className="px-4 py-3">库存</th>
                <th className="px-4 py-3">状态</th>
                <th className="px-4 py-3">销量</th>
                <th className="px-4 py-3">操作</th>
              </tr>
            </thead>
            <tbody>
              {dishes.data.records.map((dish) => (
                <tr key={dish.id} className="border-t border-stone-100">
                  <td className="px-4 py-3 font-semibold">{dish.name}</td>
                  <td className="px-4 py-3">{dish.categoryName || dish.categoryId}</td>
                  <td className="px-4 py-3">{formatMoney(dish.price)}</td>
                  <td className="px-4 py-3"><StockBadge stock={dish.stock} /></td>
                  <td className="px-4 py-3"><DishStatusBadge status={dish.status} /></td>
                  <td className="px-4 py-3">{dish.sales}</td>
                  <td className="px-4 py-3">
                    <div className="flex flex-wrap gap-2">
                      <button type="button" title="编辑" onClick={() => startEdit(dish)} className="h-9 w-9 rounded-lg bg-orange-50 text-orange-700">
                        <EditOutlined />
                      </button>
                      <button
                        type="button"
                        onClick={() => status.mutate({ id: dish.id, next: dish.status === 1 ? 0 : 1 })}
                        className="rounded-lg bg-stone-100 px-3 py-2 text-xs font-semibold text-stone-700"
                      >
                        {dish.status === 1 ? '下架' : '上架'}
                      </button>
                      <button type="button" title="删除" onClick={() => remove.mutate(dish.id)} className="h-9 w-9 rounded-lg bg-red-50 text-red-600">
                        <DeleteOutlined />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : dishes.data ? (
        <EmptyState />
      ) : null}
    </div>
  )
}
