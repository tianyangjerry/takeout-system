const API_ORIGIN = 'http://localhost:8090'

export function resolveImageUrl(url?: string) {
  if (!url) {
    return undefined
  }

  if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('data:')) {
    return url
  }

  return `${API_ORIGIN}${url.startsWith('/') ? url : `/${url}`}`
}
