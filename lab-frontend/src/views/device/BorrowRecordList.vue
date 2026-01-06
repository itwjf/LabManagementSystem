<template>
  <div class="borrow-record-list">
    <el-page-header @back="$router.back()" content="设备借用记录" />

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
      
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.returnStatus === '未归还'" type="warning">未归还</el-tag>
          <el-tag v-else-if="row.returnStatus === '已归还'" type="success">已归还</el-tag>
          <el-tag v-else-if="row.returnStatus === '异常归还'" type="danger">异常归还</el-tag>
        </template>
      </el-table-column>

      <!-- 归还操作：仅非管理员 + 未归还 -->
      <el-table-column label="操作" width="100" v-if="!isAdmin">
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyBorrowRecords, getAllBorrowRecords } from '@/api/borrowRecordList'
import { useAuth } from '@/composables/useAuth'

const { currentUser, fetchCurrentUser } = useAuth()
const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')

const records = ref([])
const loading = ref(false)

const formatDate = (isoStr) => {
  if (!isoStr) return ''
  const date = new Date(isoStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const fetchRecords = async () => {
  loading.value = true
  try {
    let res
    if (isAdmin.value) {
      res = await getAllBorrowRecords(1, 100)
    } else {
      res = await getMyBorrowRecords(1, 100)
    }
    records.value = res?.records || []
  } catch (err) {
    const msg = err.response?.data?.message || '加载借用记录失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}

const openReturnDialog = (record) => {
  console.log('准备归还:', record)
}

onMounted(() => {
  // ⚠️ fetchCurrentUser 现在是同步的！
  fetchCurrentUser()

  console.log('【当前用户信息】:', currentUser.value)
  console.log('【是否管理员】:', isAdmin.value)

  fetchRecords()
})
</script>

<style scoped>
.borrow-record-list {
  padding: 20px;
}
</style>