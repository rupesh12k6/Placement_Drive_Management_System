import { useState, useRef, useCallback, useEffect } from 'react'

export function useInfiniteScroll(fetchFn, size = 15, deps = []) {
  const [items, setItems] = useState([])
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [hasMore, setHasMore] = useState(true)
  const [totalElements, setTotalElements] = useState(null)

  const sentinelRef = useRef(null)
  const loadingRef = useRef(false)

  const fetchPage = useCallback(async (pageNum) => {
    if (loadingRef.current) return

    loadingRef.current = true
    setLoading(true)
    setError(null)

    try {
      const res = await fetchFn(pageNum, size)
      const body = res.data?.data ?? res.data

      const content = body?.content ?? (Array.isArray(body) ? body : [])
      const total = body?.totalElements ?? content.length
      const isLast = body?.last ?? (content.length < size)

      setItems(prev => pageNum === 0 ? content : [...prev, ...content])
      setHasMore(!isLast)
      setTotalElements(total)
      setPage(pageNum + 1)

    } catch (err) {
      setError(err?.response?.data?.message || 'Something went wrong')
      setHasMore(false)
    } finally {
      setLoading(false)
      loadingRef.current = false
    }
  }, [fetchFn, size, ...deps])

  const reset = useCallback(() => {
    setItems([])
    setPage(0)
    setHasMore(true)
    setTotalElements(null)
    loadingRef.current = false
    fetchPage(0)
  }, [fetchPage])

  useEffect(() => { reset() }, [reset])

  const loadMore = useCallback(() => {
    if (!loading && hasMore) fetchPage(page)
  }, [loading, hasMore, page, fetchPage])

  useEffect(() => {
    const el = sentinelRef.current
    if (!el) return

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && hasMore && !loading && items.length > 0) {
          loadMore()
        }
      },
      { threshold: 0.1 }
    )

    observer.observe(el)
    return () => observer.disconnect()
  }, [loadMore, hasMore, loading, items.length])

  return { items, loading, error, hasMore, loadMore, totalElements, reset, sentinelRef }
}

export default useInfiniteScroll