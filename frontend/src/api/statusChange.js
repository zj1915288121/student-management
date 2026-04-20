import request from '@/utils/request'

export function getPageList(params) {
  return request({ url: '/status-changes', method: 'get', params })
}

export function getById(id) {
  return request({ url: `/status-changes/${id}`, method: 'get' })
}

export function apply(data) {
  return request({ url: '/status-changes', method: 'post', data })
}

export function approve(id) {
  return request({ url: `/status-changes/${id}/approve`, method: 'put' })
}

export function reject(id, reason) {
  return request({ url: `/status-changes/${id}/reject`, method: 'put', params: { reason } })
}

export function remove(id) {
  return request({ url: `/status-changes/${id}`, method: 'delete' })
}

export function getMyList(params) {
  return request({ url: '/status-changes/my', method: 'get', params })
}
