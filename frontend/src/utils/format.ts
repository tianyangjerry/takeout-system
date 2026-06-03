export function formatMoney(value?: number | null) {
  return `¥${Number(value ?? 0).toFixed(2)}`
}

export function formatCount(value?: number | null) {
  return Number(value ?? 0).toLocaleString('zh-CN')
}

export function nonEmpty(value: string | null | undefined) {
  const trimmed = value?.trim()
  return trimmed ? trimmed : undefined
}
