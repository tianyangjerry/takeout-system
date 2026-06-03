import { UploadOutlined } from '@ant-design/icons'
import type { ChangeEvent } from 'react'

interface ImageUploadButtonProps {
  uploading?: boolean
  onSelect: (file: File) => void
}

export function ImageUploadButton({ uploading = false, onSelect }: ImageUploadButtonProps) {
  function handleChange(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0]
    if (file) {
      onSelect(file)
      event.target.value = ''
    }
  }

  return (
    <label className="inline-flex cursor-pointer items-center gap-2 rounded-lg border border-orange-200 bg-white px-4 py-2 text-sm font-semibold text-orange-700 hover:bg-orange-50">
      <UploadOutlined />
      {uploading ? '上传中' : '上传图片'}
      <input type="file" accept="image/png,image/jpeg,image/webp" className="hidden" onChange={handleChange} />
    </label>
  )
}
