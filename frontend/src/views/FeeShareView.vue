<template>
  <div class="fee-share">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button type="primary" :icon="Back" @click="router.push('/')" circle size="small" />
            <el-icon class="header-icon"><DataAnalysis /></el-icon>
            <span class="header-title">费用分摊</span>
          </div>
        </div>
      </template>
      
      <div class="calculate-section">
        <el-card shadow="never" class="calculate-card">
          <template #header>
            <div class="calculate-header">
              <!-- <el-icon><Calculator /></el-icon> -->
              <span>分摊计算</span>
            </div>
          </template>
          
          <el-form :inline="true" class="calculate-form">
            <el-form-item label="分摊月份">
              <el-date-picker v-model="shareMonth" type="month" placeholder="请选择月份" format="YYYY年MM月" value-format="YYYY-MM" style="width: 200px" />
            </el-form-item>
            <el-form-item label="分摊方式">
              <el-select v-model="shareMethod" placeholder="请选择方式" style="width: 220px">
                <el-option label="按租户数量平均分摊" value="by_tenant" />
                <el-option label="按入住天数比例分摊" value="by_day" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary"  @click="handleCalculate" :loading="calculateLoading">计算分摊</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
      
      <el-divider />
      
      <div class="result-section">
        <div class="result-header">
          <h3>分摊结果</h3>
          <el-button type="success" :icon="Download" @click="handleExport">导出Excel</el-button>
        </div>
        
        <el-table :data="tableData" style="width: 100%" stripe :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="tenantName" label="租户姓名" width="120" align="center" />
          <el-table-column prop="shareMonth" label="分摊月份" width="120" align="center">
            <template #default="scope">
              <el-tag type="info">{{ scope.row.shareMonth }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="waterFee" label="水费" width="100" align="center">
            <template #default="scope">
              <span class="fee-text">¥{{ scope.row.waterFee }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="electricityFee" label="电费" width="100" align="center">
            <template #default="scope">
              <span class="fee-text">¥{{ scope.row.electricityFee }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="gasFee" label="天然气费" width="100" align="center">
            <template #default="scope">
              <span class="fee-text">¥{{ scope.row.gasFee }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="propertyFee" label="物业费" width="100" align="center">
            <template #default="scope">
              <span class="fee-text">¥{{ scope.row.propertyFee }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalFee" label="总费用" width="120" align="center">
            <template #default="scope">
              <el-tag type="success" effect="dark" size="large">¥{{ scope.row.totalFee }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-wrapper">
          <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataAnalysis, Back, Download } from '@element-plus/icons-vue'
import { getFeeShareList, calculateFeeShare } from '@/api/feeShare'
import * as XLSX from 'xlsx'

const router = useRouter()

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const shareMonth = ref('')
const shareMethod = ref('by_tenant')
const calculateLoading = ref(false)

const loadData = async () => {
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    const response = await getFeeShareList(params)
    tableData.value = response.records
    total.value = response.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleCalculate = async () => {
  if (!shareMonth.value) {
    ElMessage.warning('请选择分摊月份')
    return
  }
  
  calculateLoading.value = true
  try {
    const response = await calculateFeeShare(shareMonth.value, shareMethod.value)
    if (response.success) {
      ElMessage.success('分摊计算成功')
      loadData()
    } else {
      ElMessage.error(response.message || '计算失败')
    }
  } catch (error) {
    ElMessage.error('计算失败')
  } finally {
    calculateLoading.value = false
  }
}

const handleExport = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  
  try {
    // 准备导出数据
    const exportData = tableData.value.map(item => ({
      'ID': item.id,
      '租户姓名': item.tenantName,
      '分摊月份': item.shareMonth,
      '水费': item.waterFee,
      '电费': item.electricityFee,
      '天然气费': item.gasFee,
      '物业费': item.propertyFee,
      '总费用': item.totalFee
    }))
    
    // 创建工作簿
    const ws = XLSX.utils.json_to_sheet(exportData)
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, '费用分摊')
    
    // 生成Excel文件的二进制数据
    const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
    
    // 创建Blob对象
    const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    
    // 创建下载链接
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `费用分摊_${new Date().toLocaleDateString()}.xlsx`
    link.style.display = 'none'
    
    // 触发下载
    document.body.appendChild(link)
    link.click()
    
    // 清理
    document.body.removeChild(link)
    setTimeout(() => URL.revokeObjectURL(url), 100)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.fee-share { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px); }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.header-left { display: flex; align-items: center; gap: 12px; }
.header-icon { font-size: 24px; color: #409eff; }
.header-title { font-size: 18px; font-weight: bold; color: #303133; }

.calculate-section { margin-bottom: 20px; }
.calculate-card { background: #f0f9ff; }
.calculate-header { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: bold; color: #409eff; }
.calculate-form { margin-top: 16px; }

.result-section { margin-top: 20px; }
.result-header { margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; }
.result-header h3 { font-size: 16px; font-weight: bold; color: #303133; margin: 0; }

.fee-text { font-size: 14px; font-weight: bold; color: #f56c6c; }
.pagination-wrapper { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
