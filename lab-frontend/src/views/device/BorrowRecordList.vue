<template>
  <div class="borrow-record-list">
    <el-page-header @back="$router.back()" content="设备借用记录" />

    <!-- 搜索表单 -->
    <el-card shadow="never" style="margin-top: 16px">
      <el-form :model="searchForm" inline>
        <el-form-item label="设备编号">
          <el-input v-model="searchForm.deviceId" placeholder="请输入设备编号" clearable style="width: 150px" />
        </el-form-item>
        
        <el-form-item label="设备名称">
          <el-input v-model="searchForm.deviceName" placeholder="请输入设备名称" clearable style="width: 150px" />
        </el-form-item>
        
        <!-- 借用人：仅管理员可见 -->
        <el-form-item v-if="isAdmin" label="借用人">
          <el-input v-model="searchForm.borrowerName" placeholder="请输入借用人姓名" clearable style="width: 120px" />
        </el-form-item>
        
        <!-- 部门：仅管理员可见 -->
        <el-form-item v-if="isAdmin" label="部门">
          <el-input v-model="searchForm.department" placeholder="请输入部门名称" clearable style="width: 150px" />
        </el-form-item>
        
        <el-form-item label="借用时间">
          <el-date-picker
            v-model="searchForm.borrowTimeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        
        <el-form-item label="归还状态">
          <el-select v-model="searchForm.returnStatus" placeholder="全部" clearable style="width: 120px">
            <el-option label="未归还" value="未归还" />
            <el-option label="已归还" value="已归还" />
            <el-option label="异常归还" value="异常归还" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table
      :data="records"
      v-loading="loading"
      style="width: 100%; margin-top: 16px"
      border
    >
      <el-table-column prop="deviceId" label="设备编号" width="150" />
      <el-table-column prop="deviceName" label="设备名称" min-width="180" />
      
      <!-- 借用人：仅管理员可见 -->
      <el-table-column prop="borrowerName" label="借用人" width="120" v-if="isAdmin" />
      
      <el-table-column prop="borrowTime" label="借用时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.borrowTime) }}
        </template>
      </el-table-column>
      
      <el-table-column prop="expectedReturnTime" label="预计归还" width="180">
        <template #default="{ row }">
          {{ formatDate(row.expectedReturnTime) }}
        </template>
      </el-table-column>
      
      <el-table-column prop="actualReturnTime" label="实际归还" width="180">
        <template #default="{ row }">
          {{ row.actualReturnTime ? formatDate(row.actualReturnTime) : '—' }}
        </template>
      </el-table-column>
      
      <!-- 状态：未归还、已归还、异常归还 -->
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.returnStatus === '未归还'" type="warning">未归还</el-tag>
          <el-tag v-else-if="row.returnStatus === '已归还'" type="success">已归还</el-tag>
          <el-tag v-else-if="row.returnStatus === '异常归还'" type="danger">异常归还</el-tag>
        </template>
      </el-table-column>

      <!-- 经办人 -->
      <el-table-column prop="handler" label="经办人" width="100" />

      <!-- 归还时设备状况 -->
      <el-table-column prop="deviceStatusOnReturn" label="归还时设备状况" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.deviceStatusOnReturn === '正常'" type="success" size="small">正常</el-tag>
          <el-tag v-else-if="row.deviceStatusOnReturn === '损坏'" type="danger" size="small">损坏</el-tag>
          <el-tag v-else-if="row.deviceStatusOnReturn === '丢失'" type="warning" size="small">丢失</el-tag>
          <el-tag v-else disabled size="small">—</el-tag>
        </template>
      </el-table-column>

      <!-- 损害描述 -->
      <el-table-column prop="damageDescription" label="损害描述" min-width="150">
        <template #default="{ row }">
          {{ row.damageDescription || '—' }}
        </template>
      </el-table-column>

      <!-- 归还登记时间 -->
      <el-table-column prop="returnTime" label="归还登记时间" width="180">
        <template #default="{ row }">
          {{ row.returnTime ? formatDate(row.returnTime) : '—' }}
        </template>
      </el-table-column>

      <!-- 归还操作：未归还的记录可归还 -->
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.returnStatus === '未归还'"
            size="small"
            type="success"
            @click="openReturnDialog(row)"
          >
            归还
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="total > 0"
      style="margin-top: 20px; text-align: right"
      v-model:current-page="page"
      v-model:page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next, sizes"
      :page-sizes="[5, 10, 20]"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    />

    <!-- 归还设备弹窗 -->
    <el-dialog v-model="returnDialogVisible" title="设备归还登记" width="500px">
      <el-form :model="returnForm" :rules="returnRules" ref="returnFormRef" label-width="120px">
        <el-form-item label="设备名称">
          {{ currentRecord?.deviceName }}
        </el-form-item>
        
        <el-form-item label="实际归还时间" prop="actualReturnTime">
          <el-date-picker
            v-model="returnForm.actualReturnTime"
            type="datetime"
            placeholder="选择实际归还时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            format="YYYY-MM-DD HH:mm:ss"
            editable
            clearable
          />
        </el-form-item>
        
        <el-form-item label="设备归还状态" prop="deviceStatusOnReturn">
          <el-radio-group v-model="returnForm.deviceStatusOnReturn">
            <el-radio label="正常">正常</el-radio>
            <el-radio label="损坏">损坏</el-radio>
            <el-radio label="丢失">丢失</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item v-if="returnForm.deviceStatusOnReturn !== '正常'" label="损坏/丢失说明" prop="damageDescription">
          <el-input 
            v-model="returnForm.damageDescription" 
            type="textarea" 
            :rows="3" 
            placeholder="请详细说明设备损坏或丢失情况"
          />
        </el-form-item>
        
        <el-form-item label="经办人" prop="handler">
          <el-input v-model="returnForm.handler" placeholder="请输入经办人姓名" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReturn">确认归还</el-button>
      </template>
    </el-dialog>
  </div>
</template>


<script setup>
import { ref, onMounted, computed, reactive } from 'vue'
import { ElMessage } from 'element-plus'
// 引入 API 函数
import { getMyBorrowRecords, getAllBorrowRecords, returnDevice } from '@/api/borrowRecordList'
import { useAuth } from '@/composables/useAuth' // 引入 useAuth 组合式函数

const { currentUser, fetchCurrentUser } = useAuth()
const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')

// 分页相关
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  deviceId: '',
  deviceName: '',
  borrowerName: '',
  department: '',
  borrowTimeRange: [],
  returnStatus: ''
})

const records = ref([])
const loading = ref(false)

// 归还弹窗相关
const returnDialogVisible = ref(false)
const returnFormRef = ref()
const currentRecord = ref(null)

const returnForm = reactive({
  id: null,
  deviceId: null,
  actualReturnTime: '',
  deviceStatusOnReturn: '正常',
  damageDescription: '',
  handler: ''
})

// 归还表单校验规则
const returnRules = {
  actualReturnTime: [{ required: true, message: '请选择实际归还时间', trigger: 'change' }],
  deviceStatusOnReturn: [{ required: true, message: '请选择设备归还状态', trigger: 'change' }],
  damageDescription: [
    { required: true, message: '请填写损坏/丢失说明', trigger: 'blur' }
  ],
  handler: [{ required: true, message: '请输入经办人', trigger: 'blur' }]
}

const formatDate = (isoStr) => {
  if (!isoStr) return ''
  const date = new Date(isoStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 加载借用记录
const fetchRecords = async () => {
  loading.value = true
  try {
    // 构建搜索参数
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      deviceId: searchForm.deviceId || undefined,
      deviceName: searchForm.deviceName || undefined,
      borrowerName: searchForm.borrowerName || undefined,
      department: searchForm.department || undefined,
      returnStatus: searchForm.returnStatus || undefined
    }
    
    // 处理时间范围
    if (searchForm.borrowTimeRange && searchForm.borrowTimeRange.length === 2) {
      params.borrowTimeStart = searchForm.borrowTimeRange[0]
      params.borrowTimeEnd = searchForm.borrowTimeRange[1]
    }
    
    let res // 借用记录响应数据
    if (isAdmin.value) { // 管理员加载所有借用记录
      res = await getAllBorrowRecords(params)
    } else { // 普通用户加载自己的借用记录
      // 普通用户只能看自己的，但也可以搜索
      res = await getMyBorrowRecords(params)
    }
    // 处理响应数据，确保 records 是数组
    records.value = res?.records || []
    total.value = res?.total || 0
  } catch (err) {
    const msg = err.response?.data?.message || '加载借用记录失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1 // 重置到第一页
  fetchRecords()
}

// 重置搜索
const handleReset = () => {
  searchForm.deviceId = ''
  searchForm.deviceName = ''
  searchForm.borrowerName = ''
  searchForm.department = ''
  searchForm.borrowTimeRange = []
  searchForm.returnStatus = ''
  page.value = 1
  fetchRecords()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  page.value = 1 // 重置到第一页
  fetchRecords()
}

// 页码改变
const handlePageChange = (val) => {
  page.value = val
  fetchRecords()
}

// 打开归还设备对话框
const openReturnDialog = (record) => {
  currentRecord.value = record
  returnForm.id = record.id
  returnForm.deviceId = record.deviceId
  returnForm.actualReturnTime = new Date()
  returnForm.deviceStatusOnReturn = '正常'
  returnForm.damageDescription = ''
  returnForm.handler = currentUser.value?.name || ''
  returnDialogVisible.value = true
}

// 提交归还
const submitReturn = async () => {
  if (!returnFormRef.value) return
  
  await returnFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await returnDevice({
          id: returnForm.id,
          deviceId: returnForm.deviceId,
          actualReturnTime: returnForm.actualReturnTime,
          deviceStatusOnReturn: returnForm.deviceStatusOnReturn,
          damageDescription: returnForm.damageDescription,
          handler: returnForm.handler
        })
        
        ElMessage.success('设备归还登记成功')
        returnDialogVisible.value = false
        fetchRecords() // 刷新借用记录列表
        
        // 触发事件通知设备列表刷新状态（如果已打开）
        window.dispatchEvent(new CustomEvent('device-status-changed'))
        
      } catch (err) {
        const msg = err.response?.data?.message || '归还登记失败'
        ElMessage.error(msg)
      }
    }
  })
}

// 组件挂载时加载借用记录
onMounted(() => {
  // fetchCurrentUser 现在是同步的！
  // 确保在加载借用记录之前获取当前用户信息
  fetchCurrentUser()

  console.log('【当前用户信息】:', currentUser.value)
  console.log('【是否管理员】:', isAdmin.value)

  // 加载借用记录
  fetchRecords()
})
</script>


<style scoped>
.borrow-record-list {
  padding: 20px;
}
</style>