import request from '@/utils/request'

export function getStats() {
  return request({ url: '/dashboard/stats', method: 'get' })
}

export function getScoreDistribution() {
  return request({ url: '/dashboard/score-distribution', method: 'get' })
}

export function getStudentStatus() {
  return request({ url: '/dashboard/student-status', method: 'get' })
}

export function getClassRanking() {
  return request({ url: '/dashboard/class-ranking', method: 'get' })
}
