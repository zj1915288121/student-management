import request from '@/utils/request'

export function getPageList(params) {
  return request({ url: '/reward-punish', method: 'get', params })
}

export function add(data) {
  return request({ url: '/reward-punish', method: 'post', data })
}

export function update(id, data) {
  return request({ url: `/reward-punish/${id}`, method: 'put', data })
}

export function remove(id) {
  return request({ url: `/reward-punish/${id}`, method: 'delete' })
}

export function getByStudentId(studentId) {
  return request({ url: `/reward-punish/student/${studentId}`, method: 'get' })
}

export function getMyRecords() {
  return request({ url: '/reward-punish/my', method: 'get' })
}
