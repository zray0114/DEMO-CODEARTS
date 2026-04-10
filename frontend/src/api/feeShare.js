import request from './request'

export function getFeeShareList(params) {
  return request({
    url: '/fee-share/list',
    method: 'get',
    params
  })
}

export function calculateFeeShare(month, method) {
  return request({
    url: '/fee-share/calculate',
    method: 'post',
    params: { month, method }
  })
}
