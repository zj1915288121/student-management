import request from '@/utils/request'

export function getPageList(params) {
  return request({ url: '/classes', method: 'get', params })
}

export function getById(id) {
  return request({ url: `/classes/${id}`, method: 'get' })
}

export function add(data) {
  return request({ url: '/classes', method: 'post', data })
}

export function update(id, data) {
  return request({ url: `/classes/${id}`, method: 'put', data })
}

export function remove(id) {
  return request({ url: `/classes/${id}`, method: 'delete' })
}

export function getAll() {
  return request({ url: '/classes/all', method: 'get' })
}
