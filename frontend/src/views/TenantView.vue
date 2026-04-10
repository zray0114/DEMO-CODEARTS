<template>
  <div class="tenant-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button type="primary" :icon="Back" @click="router.push('/')" circle size="small" />
            <el-icon class="header-icon"><User /></el-icon>
            <span class="header-title">租户管理</span>
          </div>
          <el-button type="primary" :icon="Plus" @click="showAddDialog">新增租户</el-button>
        </div>
      </template>
      
      <div class="search-bar">
        <el-form :inline="true">
          <el-form-item label="关键字搜索">
            <el-input v-model="keyword" placeholder="请输入姓名或账号" @change="loadData" clearable style="width: 300px">
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      
      <el-table :data="tableData" style="width: 100%" stripe :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120" align="center" />
        <el-table-column prop="account" label="账号" width="120" align="center" />
        <el-table-column prop="checkInDate" label="入住日期" width="140" align="center">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.checkInDate }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rentAmount" label="房租金额" width="120" align="center">
          <template #default="scope">
            <span class="amount-text">¥{{ scope.row.rentAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="depositAmount" label="押金金额" width="120" align="center">
          <template #default="scope">
            <span class="amount-text">¥{{ scope.row.depositAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" effect="dark">
              {{ scope.row.status === 1 ? '在住' : '已退租' }}
            </el-tag>
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
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" :close-on-click-modal="false">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="选择用户" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择用户" style="width: 100%" filterable>
            <el-option v-for="user in userList" :key="user.id" :label="`${user.username} (${user.account})`" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入住日期" prop="checkInDate">
          <el-date-picker v-model="form.checkInDate" type="date" placeholder="请选择入住日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="房租金额" prop="rentAmount">
          <el-input-number v-model="form.rentAmount" :precision="2" :min="0" :max="999999.99" style="width: 100%" placeholder="请输入房租金额" />
        </el-form-item>
        <el-form-item label="押金金额" prop="depositAmount">
          <el-input-number v-model="form.depositAmount" :precision="2" :min="0" :max="999999.99" style="width: 100%" placeholder="请输入押金金额" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">在住</el-radio>
            <el-radio :value="0">已退租</el-radio>
          </el-radio-group>
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
import { Plus, Edit, Delete, User, Search, Back } from '@element-plus/icons-vue'
import { getTenantList, createTenant, updateTenant, deleteTenant } from '@/api/tenant'
import { getUserList } from '@/api/user'

const router = useRouter()

const tableData = ref([])
const userList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('新增租户')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = ref({ id: null, userId: null, checkInDate: '', rentAmount: 0, depositAmount: 0, status: 1 })
const rules = {
  userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
  rentAmount: [{ required: true, message: '请输入房租金额', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const params = { page: currentPage.value, size: pageSize.value, keyword: keyword.value }
    const response = await getTenantList(params)
    tableData.value = response.records
    total.value = response.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const loadUsers = async () => {
  try {
    userList.value = await getUserList()
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '新增租户'
  form.value = { id: null, userId: null, checkInDate: '', rentAmount: 0, depositAmount: 0, status: 1 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑租户'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该租户吗？', '提示', { type: 'warning' })
    await deleteTenant(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateTenant(form.value.id, form.value)
          ElMessage.success('更新成功')
        } else {
          await createTenant(form.value)
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
  loadUsers()
})
</script>

<style scoped>
.tenant-management { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px); }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.header-left { display: flex; align-items: center; gap: 12px; }
.header-icon { font-size: 24px; color: #409eff; }
.header-title { font-size: 18px; font-weight: bold; color: #303133; }
.search-bar { margin-bottom: 20px; padding: 16px; background: #f9fafc; border-radius: 4px; }
.amount-text { font-size: 16px; font-weight: bold; color: #f56c6c; }
.pagination-wrapper { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
