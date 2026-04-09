<template>
  <div class="update-password-container">
    <div class="update-password-wrapper">
      <div class="update-password-header">
        <h2>更新密码</h2>
        <p class="update-reason">{{ reasonText }}</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="update-password-form"
      >
        <el-form-item prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="handleUpdate"
            :loading="loading"
            class="update-button"
          >
            更新密码
          </el-button>
          <el-button
            size="large"
            @click="handleCancel"
            class="cancel-button"
          >
            返回登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock } from '@element-plus/icons-vue'
import { updatePassword } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const reasonText = computed(() => {
  const reason = route.query.reason
  if (reason === 'DEFAULT_PASSWORD') {
    return '您正在使用默认密码，为了账户安全，请立即修改密码'
  } else if (reason === 'PASSWORD_EXPIRED') {
    return '您的密码已超过6个月未更新，请修改密码以继续使用'
  }
  return '请更新您的密码'
})

const handleUpdate = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const userId = route.query.userId
        const response = await updatePassword({
          userId: userId,
          newPassword: form.newPassword
        })
        
        if (response.success) {
          ElMessage.success('密码更新成功，请重新登录')
          handleCancel()
        } else {
          ElMessage.error(response.message || '密码更新失败')
        }
      } catch (error) {
        ElMessage.error('密码更新失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleCancel = () => {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.update-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.update-password-wrapper {
  width: 100%;
  max-width: 440px;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.update-password-header {
  text-align: center;
  margin-bottom: 32px;
}

.update-password-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 12px 0;
}

.update-reason {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.6;
}

.update-password-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e0e0e0;
  transition: all 0.3s;
}

.update-password-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #3B82F6;
}

.update-password-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #3B82F6;
}

.update-button {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  border: none;
  margin-bottom: 12px;
}

.update-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
}

.cancel-button {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
}
</style>
