import request from '@/utils/request'

export function getPageList(params) {
  return request({ url: '/scores', method: 'get', params })
}

export function add(data) {
  return request({ url: '/scores', method: 'post', data })
}

export function batchAdd(data) {
  return request({ url: '/scores/batch', method: 'post', data })
}

export function update(id, data) {
  return request({ url: `/scores/${id}`, method: 'put', data })
}

export function remove(id) {
  return request({ url: `/scores/${id}`, method: 'delete' })
}

export function getMyScores() {
  return request({ url: '/scores/my', method: 'get' })
}

export function getStats(params) {
  return request({ url: '/scores/stats', method: 'get', params })
}

export function getRanking(params) {
  return request({ url: '/scores/ranking', method: 'get', params })
}

export function getWarning(params) {
  return request({ url: '/scores/warning', method: 'get', params })
}

export function exportExcel(params) {
  return request({ url: '/scores/export', method: 'get', params, responseType: 'blob' })
}
