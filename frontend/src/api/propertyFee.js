import request from './request'

export function getPropertyFeeList(params) {
  return request({
    url: '/property-fee/list',
    method: 'get',
    params
  })
}

export function createPropertyFee(data) {
  return request({
    url: '/property-fee',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function updatePropertyFee(id, data) {
  return request({
    url: `/property-fee/${id}`,
    method: 'put',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deletePropertyFee(id) {
  return request({
    url: `/property-fee/${id}`,
    method: 'delete'
  })
}

export function getPropertyFeeStatistics(year) {
  return request({
    url: '/property-fee/statistics',
    method: 'get',
    params: { year }
  })
}
