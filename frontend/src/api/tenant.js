import request from './request'

export function getTenantList(params) {
  return request({
    url: '/tenant/list',
    method: 'get',
    params
  })
}

export function createTenant(data) {
  return request({
    url: '/tenant',
    method: 'post',
    data
  })
}

export function updateTenant(id, data) {
  return request({
    url: `/tenant/${id}`,
    method: 'put',
    data
  })
}

export function deleteTenant(id) {
  return request({
    url: `/tenant/${id}`,
    method: 'delete'
  })
}

export function getActiveTenants() {
  return request({
    url: '/tenant/active',
    method: 'get'
  })
}
