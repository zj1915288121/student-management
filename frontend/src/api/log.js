import request from '@/utils/request'

export function getPageList(params) {
  return request({ url: '/logs', method: 'get', params })
}
