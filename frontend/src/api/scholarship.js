import request from '@/utils/request'

export function getPageList(params) {
  return request({ url: '/scholarships', method: 'get', params })
}

export function getById(id) {
  return request({ url: `/scholarships/${id}`, method: 'get' })
}

export function add(data) {
  return request({ url: '/scholarships', method: 'post', data })
}

export function update(id, data) {
  return request({ url: `/scholarships/${id}`, method: 'put', data })
}

export function remove(id) {
  return request({ url: `/scholarships/${id}`, method: 'delete' })
}

export function toggleStatus(id) {
  return request({ url: `/scholarships/${id}/toggle`, method: 'put' })
}

export function getAll() {
  return request({ url: '/scholarships/all', method: 'get' })
}

export function getApplyPageList(params) {
  return request({ url: '/scholarships/apply', method: 'get', params })
}

export function apply(data) {
  return request({ url: '/scholarships/apply', method: 'post', data })
}

export function approve(id) {
  return request({ url: `/scholarships/apply/${id}/approve`, method: 'put' })
}

export function reject(id, reason) {
  return request({ url: `/scholarships/apply/${id}/reject`, method: 'put', params: { reason } })
}

export function getMyApplyList(params) {
  return request({ url: '/scholarships/apply/my', method: 'get', params })
}
