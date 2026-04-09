<template>
  <div class="home-container">
    <header class="app-header">
      <div class="header-content">
        <div class="logo-section">
          <svg class="logo-icon" viewBox="0 0 40 40" fill="none">
            <rect width="40" height="40" rx="8" fill="url(#grad)"/>
            <path d="M12 20L18 26L28 14" stroke="white" stroke-width="3" stroke-linecap="round"/>
            <defs>
              <linearGradient id="grad" x1="0" y1="0" x2="40" y2="40">
                <stop stop-color="#3B82F6"/>
                <stop offset="1" stop-color="#8B5CF6"/>
              </linearGradient>
            </defs>
          </svg>
          <h1 class="app-title">用户管理系统</h1>
        </div>
        
        <div class="user-section">
          <div v-if="currentUser" class="user-info">
            <div class="user-avatar">{{ currentUser.username.charAt(0).toUpperCase() }}</div>
            <span class="user-name">{{ currentUser.username }}</span>
            <el-button type="danger" size="small" @click="handleLogout" plain>退出</el-button>
          </div>
          <el-button v-else type="primary" @click="goToLogin">登录</el-button>
        </div>
      </div>
    </header>

    <main class="main-content">
      <div class="welcome-section">
        <h2 class="welcome-title">欢迎使用</h2>
        <p class="welcome-desc">基于 Vue3 + Spring Boot + MySQL 构建的全栈用户管理解决方案</p>
        
        <div class="tech-stack">
          <div class="tech-item">
            <div class="tech-icon">⚡</div>
            <div class="tech-name">Vue 3</div>
          </div>
          <div class="tech-item">
            <div class="tech-icon">🍃</div>
            <div class="tech-name">Spring Boot</div>
          </div>
          <div class="tech-item">
            <div class="tech-icon">🗄️</div>
            <div class="tech-name">MySQL</div>
          </div>
        </div>
      </div>

      <div class="action-cards">
        <div class="action-card" @click="goToUsers">
          <div class="card-icon">👥</div>
          <h3 class="card-title">用户管理</h3>
          <p class="card-desc">管理系统用户，支持增删改查操作</p>
          <div class="card-arrow">→</div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const currentUser = ref(null)

const loadUser = () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  }
}

const goToUsers = () => {
  router.push('/users')
}

const goToLogin = () => {
  router.push('/login')
}

const handleLogout = () => {
  localStorage.removeItem('user')
  currentUser.value = null
  router.push('/login')
}

onMounted(() => {
  loadUser()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.app-header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
}

.app-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
  letter-spacing: -0.3px;
}

.user-section {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.user-name {
  font-weight: 500;
  color: #1a1a1a;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 24px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 48px;
}

.welcome-title {
  font-size: 48px;
  font-weight: 800;
  color: #1a1a1a;
  margin: 0 0 16px 0;
  letter-spacing: -1px;
}

.welcome-desc {
  font-size: 18px;
  color: #666;
  margin: 0 0 32px 0;
}

.tech-stack {
  display: flex;
  justify-content: center;
  gap: 32px;
}

.tech-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.tech-icon {
  font-size: 32px;
}

.tech-name {
  font-size: 14px;
  font-weight: 600;
  color: #666;
}

.action-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.action-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  border-color: #3B82F6;
}

.card-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.card-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.card-desc {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.card-arrow {
  position: absolute;
  right: 24px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 24px;
  color: #3B82F6;
  opacity: 0;
  transition: all 0.3s;
}

.action-card:hover .card-arrow {
  opacity: 1;
  right: 16px;
}
</style>
