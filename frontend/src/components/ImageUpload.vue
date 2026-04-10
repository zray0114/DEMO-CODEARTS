<template>
  <div class="image-upload">
    <el-upload
      v-model:file-list="fileList"
      action="#"
      list-type="picture-card"
      :auto-upload="false"
      :on-change="handleChange"
      :on-remove="handleRemove"
      :limit="limit"
      :accept="'image/*'"
    >
      <el-icon><Plus /></el-icon>
      <template #tip>
        <div class="el-upload__tip">
          最多上传 {{ limit }} 张图片，支持 jpg/png 格式
        </div>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  limit: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['update:modelValue'])

const fileList = ref([])

// 监听外部传入的图片数据
watch(() => props.modelValue, (newVal) => {
  if (newVal && Array.isArray(newVal) && newVal.length > 0) {
    fileList.value = newVal.map((img, index) => ({
      name: `image-${index}`,
      url: img.url || URL.createObjectURL(new Blob([img])),
      raw: img.raw
    }))
  } else {
    // 如果是编辑模式且已有图片（byte[]），显示提示
    fileList.value = []
  }
}, { immediate: true })

const handleChange = (file, files) => {
  // 验证文件类型
  const isImage = file.raw.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  
  // 验证文件大小（限制5MB）
  const isLt5M = file.raw.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  
  fileList.value = files
  updateModelValue()
}

const handleRemove = (file, files) => {
  fileList.value = files
  updateModelValue()
}

const updateModelValue = () => {
  // 返回文件对象数组
  const files = fileList.value.map(file => file.raw).filter(Boolean)
  emit('update:modelValue', files)
}

// 暴露方法供父组件调用
defineExpose({
  getFileList: () => fileList.value
})
</script>

<style scoped>
.image-upload {
  width: 100%;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}
</style>
