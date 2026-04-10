import request from './request'

export function getWaterFeeList(params) {
  return request({
    url: '/water-fee/list',
    method: 'get',
    params
  })
}

export function createWaterFee(data) {
  return request({
    url: '/water-fee',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function updateWaterFee(id, data) {
  return request({
    url: `/water-fee/${id}`,
    method: 'put',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deleteWaterFee(id) {
  return request({
    url: `/water-fee/${id}`,
    method: 'delete'
  })
}

export function getWaterFeeStatistics(year) {
  return request({
    url: '/water-fee/statistics',
    method: 'get',
    params: { year }
  })
}
