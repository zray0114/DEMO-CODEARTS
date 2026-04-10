import request from './request'

export function getGasFeeList(params) {
  return request({
    url: '/gas-fee/list',
    method: 'get',
    params
  })
}

export function createGasFee(data) {
  return request({
    url: '/gas-fee',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function updateGasFee(id, data) {
  return request({
    url: `/gas-fee/${id}`,
    method: 'put',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deleteGasFee(id) {
  return request({
    url: `/gas-fee/${id}`,
    method: 'delete'
  })
}

export function getGasFeeStatistics(year) {
  return request({
    url: '/gas-fee/statistics',
    method: 'get',
    params: { year }
  })
}
