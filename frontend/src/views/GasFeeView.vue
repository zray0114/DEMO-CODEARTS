<template>
  <div class="fee-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button type="primary" :icon="Back" @click="router.push('/')" circle size="small" />
            <el-icon class="header-icon"><Money /></el-icon>
            <span class="header-title">天然气费管理</span>
          </div>
          <el-button type="primary" :icon="Plus" @click="showAddDialog">新增记录</el-button>
        </div>
      </template>
      
      <div class="search-bar">
        <el-form :inline="true">
          <el-form-item label="缴费月份">
            <el-date-picker
              v-model="searchMonth"
              type="month"
              placeholder="请选择月份"
              format="YYYY年MM月"
              value-format="YYYY-MM"
              @change="loadData"
              clearable
            />
          </el-form-item>
        </el-form>
      </div>
      
      <el-table 
        :data="tableData" 
        style="width: 100%"
        stripe
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="paymentDate" label="缴费日期" width="140" align="center">
          <template #default="scope">
            <el-tag>{{ scope.row.paymentDate }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="缴费金额" width="140" align="center">
          <template #default="scope">
            <span class="amount-text">¥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="图片" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.images" type="success" size="small">已上传</el-tag>
            <el-tag v-else type="info" size="small">无</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
    
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="缴费日期" prop="paymentDate">
          <el-date-picker
            v-model="form.paymentDate"
            type="date"
            placeholder="请选择缴费日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="缴费金额" prop="amount">
          <el-input-number 
            v-model="form.amount" 
            :precision="2" 
            :min="0"
            :max="999999.99"
            style="width: 100%"
            placeholder="请输入缴费金额"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注信息"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="上传图片">
          <ImageUpload v-model="form.images" :limit="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Money, Back } from '@element-plus/icons-vue'
import { getGasFeeList, createGasFee, updateGasFee, deleteGasFee } from '@/api/gasFee'
import ImageUpload from '@/components/ImageUpload.vue'

const router = useRouter()

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchMonth = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('新增记录')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = ref({
  id: null,
  paymentDate: '',
  amount: 0,
  remark: '',
  images: []
})

const rules = {
  paymentDate: [{ required: true, message: '请选择缴费日期', trigger: 'change' }],
  amount: [{ required: true, message: '请输入缴费金额', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      month: searchMonth.value
    }
    const response = await getGasFeeList(params)
    tableData.value = response.records
    total.value = response.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '新增天然气费记录'
  form.value = {
    id: null,
    paymentDate: '',
    amount: 0,
    remark: '',
    images: []
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑天然气费记录'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该水费记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteGasFee(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 创建FormData对象
        const formData = new FormData()
        formData.append('paymentDate', form.value.paymentDate)
        formData.append('amount', form.value.amount)
        formData.append('remark', form.value.remark || '')
        
        // 添加图片文件
        if (form.value.images && form.value.images.length > 0) {
          form.value.images.forEach((file, index) => {
            formData.append('images', file)
          })
        }
        
        if (isEdit.value) {
          await updateGasFee(form.value.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createGasFee(formData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.fee-management {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
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

.search-bar {
  margin-bottom: 20px;
  padding: 16px;
  background: #f9fafc;
  border-radius: 4px;
}

.amount-text {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
