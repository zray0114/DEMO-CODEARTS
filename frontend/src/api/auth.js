import request from './request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function updatePassword(data) {
  return request({
    url: '/auth/update-password',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function checkAccount(account) {
  return request({
    url: '/auth/check-account',
    method: 'get',
    params: { account }
  })
}
