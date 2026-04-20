import request from '@/utils/request'

export function getPageList(params) {
  return request({ url: '/students', method: 'get', params })
}

export function getById(id) {
  return request({ url: `/students/${id}`, method: 'get' })
}

export function add(data) {
  return request({ url: '/students', method: 'post', data })
}

export function update(id, data) {
  return request({ url: `/students/${id}`, method: 'put', data })
}

export function remove(id) {
  return request({ url: `/students/${id}`, method: 'delete' })
}

export function batchRemove(ids) {
  return request({ url: '/students/batch', method: 'delete', params: { ids } })
}

export function getStats() {
  return request({ url: '/students/stats', method: 'get' })
}

export function searchStudents(keyword) {
  return request({ url: '/students/search', method: 'get', params: { keyword } })
}

export function importExcel(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({ url: '/students/import', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
}

export function exportExcel(params) {
  return request({ url: '/students/export', method: 'get', params, responseType: 'blob' })
}

export function downloadTemplate() {
  return request({ url: '/students/template', method: 'get', responseType: 'blob' })
}
