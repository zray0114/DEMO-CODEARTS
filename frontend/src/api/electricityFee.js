import request from './request'

export function getElectricityFeeList(params) {
  return request({
    url: '/electricity-fee/list',
    method: 'get',
    params
  })
}

export function createElectricityFee(data) {
  return request({
    url: '/electricity-fee',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function updateElectricityFee(id, data) {
  return request({
    url: `/electricity-fee/${id}`,
    method: 'put',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deleteElectricityFee(id) {
  return request({
    url: `/electricity-fee/${id}`,
    method: 'delete'
  })
}

export function getElectricityFeeStatistics(year) {
  return request({
    url: '/electricity-fee/statistics',
    method: 'get',
    params: { year }
  })
}
