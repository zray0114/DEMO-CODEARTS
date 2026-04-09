<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-left">
        <div class="brand-section">
          <div class="logo">
            <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="40" height="40" rx="8" fill="url(#gradient)"/>
              <path d="M12 20L18 26L28 14" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
              <defs>
                <linearGradient id="gradient" x1="0" y1="0" x2="40" y2="40">
                  <stop stop-color="#3B82F6"/>
                  <stop offset="1" stop-color="#8B5CF6"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h1 class="brand-title">用户管理系统</h1>
          <p class="brand-subtitle">安全 · 高效 · 智能</p>
        </div>
        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">🔒</div>
            <div class="feature-text">安全认证</div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">⚡</div>
            <div class="feature-text">快速响应</div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">📊</div>
            <div class="feature-text">数据管理</div>
          </div>
        </div>
      </div>
      
      <div class="login-right">
        <div class="login-form-wrapper">
          <h2 class="form-title">{{ isRegister ? '注册账户' : '登录账户' }}</h2>
          <p class="form-subtitle">{{ isRegister ? '创建您的新账户' : '请输入您的账户信息' }}</p>
          
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            class="login-form"
          >
            <el-form-item prop="account">
              <el-input
                v-model="form.account"
                placeholder="请输入账户"
                size="large"
                :prefix-icon="User"
                @blur="handleAccountBlur"
              />
            </el-form-item>
            
            <el-form-item v-if="isRegister" prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名（可选）"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            
            <el-form-item v-if="isRegister" prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请确认密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                @click="isRegister ? handleRegister() : handleLogin()"
                :loading="loading"
                class="login-button"
              >
                {{ loading ? (isRegister ? '注册中...' : '登录中...') : (isRegister ? '注册' : '登录') }}
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="switch-mode">
            <span>{{ isRegister ? '已有账户？' : '没有账户？' }}</span>
            <el-button type="text" @click="toggleMode">
              {{ isRegister ? '立即登录' : '立即注册' }}
            </el-button>
          </div>
          
          <div v-if="!isRegister" class="login-tips">
            <span class="tip-label">测试账户：</span>
            <span class="tip-value">admin / admin</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login, register, checkAccount } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const isRegister = ref(false)
const accountExists = ref(false)

const form = reactive({
  account: '',
  username: '',
  password: '',
  confirmPassword: ''
})

const rules = computed(() => ({
  account: [
    { required: true, message: '请输入账户', trigger: 'blur' },
    { min: 3, max: 20, message: '账户长度3-20个字符', trigger: 'blur' }
  ],
  username: isRegister.value ? [
    { max: 50, message: '用户名最多50个字符', trigger: 'blur' }
  ] : [],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: isRegister.value ? [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ] : []
}))

const toggleMode = () => {
  isRegister.value = !isRegister.value
  form.account = ''
  form.username = ''
  form.password = ''
  form.confirmPassword = ''
  accountExists.value = false
}

const handleAccountBlur = async () => {
  if (!isRegister.value || !form.account || form.account.length < 3) return
  
  try {
    const response = await checkAccount(form.account)
    accountExists.value = response.exists
    if (response.exists) {
      ElMessage.warning('该账户已存在')
    }
  } catch (error) {
    console.error('检查账户失败', error)
  }
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await login({
          account: form.account,
          password: form.password
        })
        if (response.success) {
          if (response.needUpdatePassword) {
            router.push({
              path: '/update-password',
              query: {
                userId: response.userId,
                reason: response.updateReason
              }
            })
            return
          }
          
          ElMessage.success('登录成功')
          localStorage.setItem('user', JSON.stringify({
            userId: response.userId,
            username: response.username,
            account: response.account,
            token: response.token
          }))
          router.push('/')
        } else {
          ElMessage.error(response.message || '登录失败')
        }
      } catch (error) {
        ElMessage.error('登录失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  if (accountExists.value) {
    ElMessage.error('该账户已存在，请更换账户')
    return
  }
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await register({
          account: form.account,
          password: form.password,
          username: form.username
        })
        if (response.success) {
          ElMessage.success('注册成功，请登录')
          toggleMode()
        } else {
          ElMessage.error(response.message || '注册失败')
        }
      } catch (error) {
        ElMessage.error('注册失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-wrapper {
  display: flex;
  width: 100%;
  max-width: 1000px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
}

.brand-section {
  margin-bottom: 60px;
}

.logo {
  width: 60px;
  height: 60px;
  margin-bottom: 24px;
}

.logo svg {
  width: 100%;
  height: 100%;
}

.brand-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
  font-weight: 300;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.feature-icon {
  font-size: 24px;
}

.feature-text {
  font-size: 15px;
  font-weight: 500;
}

.login-right {
  flex: 1;
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-form-wrapper {
  width: 100%;
  max-width: 360px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.form-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0 0 32px 0;
}

.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e0e0e0;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #3B82F6;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #3B82F6;
}

.login-button {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
}

.switch-mode {
  text-align: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: #666;
}

.switch-mode .el-button {
  font-size: 14px;
  padding: 0 8px;
}

.login-tips {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 10px;
  font-size: 13px;
}

.tip-label {
  color: #666;
}

.tip-value {
  color: #3B82F6;
  font-weight: 600;
  margin-left: 4px;
}

@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
  }
  
  .login-left {
    padding: 40px 30px;
  }
  
  .login-right {
    padding: 40px 30px;
  }
  
  .brand-title {
    font-size: 24px;
  }
}
</style>
