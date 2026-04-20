import request from '@/utils/request'

export function getPageList(params) {
  return request({ url: '/courses', method: 'get', params })
}

export function getById(id) {
  return request({ url: `/courses/${id}`, method: 'get' })
}

export function add(data) {
  return request({ url: '/courses', method: 'post', data })
}

export function update(id, data) {
  return request({ url: `/courses/${id}`, method: 'put', data })
}

export function remove(id) {
  return request({ url: `/courses/${id}`, method: 'delete' })
}

export function getAll() {
  return request({ url: '/courses/all', method: 'get' })
}
