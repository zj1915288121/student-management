import request from '@/utils/request'

export function getPageList(params) {
  return request({ url: '/users', method: 'get', params })
}

export function getById(id) {
  return request({ url: `/users/${id}`, method: 'get' })
}

export function add(data) {
  return request({ url: '/users', method: 'post', data })
}

export function update(id, data) {
  return request({ url: `/users/${id}`, method: 'put', data })
}

export function remove(id) {
  return request({ url: `/users/${id}`, method: 'delete' })
}

export function resetPassword(id) {
  return request({ url: `/users/${id}/reset-password`, method: 'put' })
}

export function toggleStatus(id) {
  return request({ url: `/users/${id}/toggle-status`, method: 'put' })
}

export function getRoles() {
  return request({ url: '/users/roles', method: 'get' })
}

export function getRoleCounts() {
  return request({ url: '/users/role-counts', method: 'get' })
}

export function getProfile() {
  return request({ url: '/users/profile', method: 'get' })
}

export function updateProfile(data) {
  return request({ url: '/users/profile', method: 'put', data })
}

export function changePassword(data) {
  return request({ url: '/users/change-password', method: 'put', data })
}
