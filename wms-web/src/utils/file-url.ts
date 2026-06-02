/**
 * Normalize file URLs returned by backend storage.
 */
export function resolveFileUrl(url?: string | null): string {
  if (!url) {
    return ''
  }
  if (
    url.startsWith('http://')
    || url.startsWith('https://')
    || url.startsWith('data:')
    || url.startsWith('blob:')
  ) {
    return url
  }
  if (url.startsWith('/api/')) {
    return url
  }
  if (url.startsWith('/storage/')) {
    return `/api${url}`
  }
  if (url.startsWith('storage/')) {
    return `/api/${url}`
  }
  if (url.startsWith('/')) {
    return `/api${url}`
  }
  return `/api/${url}`
}
