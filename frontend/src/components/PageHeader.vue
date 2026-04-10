<template>
  <div class="page-header">
    <el-button type="primary" :icon="Back" @click="goBack" circle class="back-btn" />
    <div class="header-content">
      <el-icon class="header-icon"><component :is="icon" /></el-icon>
      <span class="header-title">{{ title }}</span>
    </div>
    <el-button type="primary" @click="showAddDialog" v-if="showAdd">
      <el-icon><Plus /></el-icon>
      <span>{{ addText }}</span>
    </el-button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Back, Plus } from '@element-plus/icons-vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  icon: {
    type: Object,
    required: true
  },
  showAdd: {
    type: Boolean,
    default: false
  },
  addText: {
    type: String,
    default: '新增'
  }
})

const emit = defineEmits(['add'])

const router = useRouter()

const goBack = () => {
  router.push('/')
}

const showAddDialog = () => {
  emit('add')
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.back-btn {
  margin-right: 12px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.header-icon {
  font-size: 24px;
  color: #409eff;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}
</style>
