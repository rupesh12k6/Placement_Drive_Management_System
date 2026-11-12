export const getNumber = (res) =>
  typeof res === 'number' ? res : res ?? 0

export const getList = (res) => {
  if (Array.isArray(res)) return res
  if (Array.isArray(res?.content)) return res.content
  return []
}