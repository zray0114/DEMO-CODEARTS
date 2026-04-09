import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/update-password',
      name: 'updatePassword',
      component: () => import('@/views/UpdatePasswordView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/users',
      name: 'users',
      component: () => import('@/views/UserView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫：未登录自动跳转到登录页面
router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('user')
  const isLoggedIn = !!userStr
  
  // 如果路由需要认证且用户未登录
  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
  } 
  // 如果用户已登录且访问登录页，重定向到首页
  else if (to.path === '/login' && isLoggedIn) {
    next('/')
  }
  else {
    next()
  }
})

export default router
