<template>
  <div>
    <!-- 卡片容器 -->
    <el-card shadow="never">
      <template #header>
          <div class="card-header">
            <span>资产出库记录</span>
            <el-button v-if="isAdmin" type="primary" @click="handleAdd">新增出库</el-button>
          </div>
        </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline>
        <el-form-item label="资产名称">
          <el-input v-model="searchForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>

        <!-- 实训室筛选 -->
        <el-form-item label="所属实训室">
          <el-select
            v-model="searchForm.labId"
            placeholder="全部实训室"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="lab in labs"
              :key="lab.labId"
              :label="lab.labName"
              :value="lab.labId"
            />
          </el-select>
        </el-form-item>

        <!-- 出库原因筛选 -->
        <el-form-item label="出库原因">
          <el-input v-model="searchForm.outReason" placeholder="请输入出库原因" clearable />
        </el-form-item>

        <!-- 审批状态筛选 -->
        <el-form-item label="审批状态">
          <el-select
            v-model="searchForm.approvalStatus"
            placeholder="全部状态"
            clearable
            style="width: 150px"
          >
            <el-option label="待审批" value="待审批" />
            <el-option label="已审批" value="已审批" />
            <el-option label="已拒绝" value="已拒绝" />
          </el-select>
        </el-form-item>

        <!-- 出库日期范围 -->
        <el-form-item label="出库日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            clearable
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="()=>loadData()">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" style="width: 100%; margin-top: 20px" v-loading="loading">
        <el-table-column prop="recordId" label="记录ID" width="100" />
        <el-table-column prop="assetName" label="资产名称" min-width="150">
          <template #default="{ row }">
            {{ row.assetName ? row.assetName : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="labName" label="所属实训室" width="120">
          <template #default="{ row }">
            {{ row.labName ? row.labName : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="出库数量" width="100" />
        <el-table-column prop="outReason" label="出库原因" min-width="150">
          <template #default="{ row }">
            {{ row.outReason ? row.outReason : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="outDate" label="出库日期" width="180">
          <template #default="{ row }">
            {{ row.outDate ? formatDateTime(row.outDate) : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="经办人" width="120">
          <template #default="{ row }">
            {{ row.handlerName ? row.handlerName : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="approverName" label="审批人" width="120">
          <template #default="{ row }">
            {{ row.approverName ? row.approverName : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="approvalStatus" label="审批状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="
                row.approvalStatus === '已审批'
                  ? 'success'
                  : row.approvalStatus === '待审批'
                  ? 'warning'
                  : 'danger'
              "
            >
              {{ row.approvalStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200">
          <template #default="{ row }">
            {{ row.remark ? row.remark : '—' }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <!-- 仅管理员可见操作 删除-->
            <el-button v-if="isAdmin" size="small" type="danger" link @click="handleDelete(row.recordId)">删除</el-button>
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
        @current-change="loadData"
      />
    </el-card>

    <!-- 弹窗：新增出库记录 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="资产ID" prop="assetId">
          <el-input v-model="form.assetId" placeholder="请输入资产ID" />
        </el-form-item>
        <el-form-item label="实训室ID" prop="labId">
          <el-select v-model="form.labId" placeholder="请选择所属实训室">
            <el-option
              v-for="lab in labs"
              :key="lab.labId"
              :label="lab.labName"
              :value="lab.labId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出库数量" prop="quantity">
          <el-input v-model.number="form.quantity" placeholder="请输入出库数量" />
        </el-form-item>
        <el-form-item label="出库原因" prop="outReason">
          <el-input v-model="form.outReason" placeholder="请输入出库原因" />
        </el-form-item>
        <el-form-item label="出库日期" prop="outDate">
          <el-date-picker
            v-model="form.outDate"
            type="datetime"
            placeholder="选择日期时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
          />
        </el-form-item>
        <el-form-item label="经办人ID" prop="handlerId">
          <el-input v-model="form.handlerId" placeholder="请输入经办人ID" />
        </el-form-item>
        <el-form-item label="经办人姓名" prop="handlerName">
          <el-input v-model="form.handlerName" placeholder="请输入经办人姓名" />
        </el-form-item>
        <el-form-item label="审批人ID" prop="approverId">
          <el-input v-model="form.approverId" placeholder="请输入审批人ID" />
        </el-form-item>
        <el-form-item label="审批人姓名" prop="approverName">
          <el-input v-model="form.approverName" placeholder="请输入审批人姓名" />
        </el-form-item>
        <el-form-item label="审批状态" prop="approvalStatus">
          <el-select v-model="form.approvalStatus" placeholder="请选择审批状态">
            <el-option label="待审批" value="待审批" />
            <el-option label="已审批" value="已审批" />
            <el-option label="已拒绝" value="已拒绝" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuth } from '@/composables/useAuth' // 引入权限控制
import * as assetOutRecordApi from '@/api/assetOutRecord' // 资产出库记录API
import * as labInfoApi from '@/api/labInfo' // 实训室API

// 获取当前用户信息
const { currentUser, fetchCurrentUser } = useAuth()
const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')

// 分页 & 搜索
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({ 
  assetId: null,
  assetName: '',
  labId: null,
  outReason: '',
  approvalStatus: '',
  outDateStart: '',
  outDateEnd: ''
})
const dateRange = ref([])
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 实训室数据
const labs = ref([])

// 弹窗
const dialogVisible = ref(false)
const editMode = ref(false)
const form = reactive({
  recordId: null,
  labId: null,
  assetId: null,
  quantity: 0,
  outReason: '',
  outDate: null,
  handlerId: null,
  handlerName: '',
  approverId: null,
  approverName: '',
  approvalStatus: '待审批',
  remark: ''
})

const formRef = ref()

// 计算弹窗标题
const dialogTitle = computed(() => (editMode.value ? '编辑出库记录' : '新增出库记录'))

// 表单校验规则
const rules = {
  assetId: [{ required: true, message: '请输入资产ID', trigger: 'blur' }],
  labId: [{ required: true, message: '请选择所属实训室', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入出库数量', trigger: 'blur' }, { type: 'number', message: '出库数量必须为数字', trigger: 'blur' }],
  outReason: [{ required: true, message: '请输入出库原因', trigger: 'blur' }],
  outDate: [{ required: true, message: '请选择出库日期', trigger: 'change' }],
  handlerName: [{ required: true, message: '请输入经办人姓名', trigger: 'blur' }],
  approvalStatus: [{ required: true, message: '请选择审批状态', trigger: 'change' }]
}

// 监听日期范围变化
watch(dateRange, (newValue) => {
  if (newValue && newValue.length === 2) {
    searchForm.outDateStart = newValue[0]
    searchForm.outDateEnd = newValue[1]
  } else {
    searchForm.outDateStart = ''
    searchForm.outDateEnd = ''
  }
})

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return ''
  const date = new Date(datetime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 加载数据
const loadData = async (currentPage = page.value) => {
  // 防御性处理（防止传入 event）
  if (typeof currentPage !== 'number') currentPage = page.value

  loading.value = true
  try {
    const res = await assetOutRecordApi.getOutRecordsPage(currentPage, pageSize.value, searchForm)
    // 响应拦截器已处理Result结构，直接使用res.records
    tableData.value = res?.records || []
    total.value = res?.total || 0
    page.value = currentPage
  } catch (err) {
    ElMessage.error('加载失败：' + (err.response?.data?.message || err.message))
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  // 清空所有条件
  searchForm.assetId = null
  searchForm.assetName = ''
  searchForm.labId = null
  searchForm.outReason = ''
  searchForm.approvalStatus = ''
  searchForm.outDateStart = ''
  searchForm.outDateEnd = ''
  dateRange.value = []
  loadData(1)
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  loadData(1)
}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该出库记录吗？', '提示', { type: 'warning' }).then(async () => {
    await assetOutRecordApi.deleteOutRecord(id)
    ElMessage.success('删除成功')
    loadData()
  })
}

// 新增
const handleAdd = () => {
  editMode.value = false
  // 默认当前时间为出库时间
  const now = new Date()
  now.setMinutes(now.getMinutes(), 0, 0) // 清除秒和毫秒
  
  Object.assign(form, {
    recordId: null,
    labId: null,
    assetId: null,
    quantity: 0,
    outReason: '',
    outDate: now.toISOString().slice(0, 16).replace('T', ' '), // 格式化为YYYY-MM-DD HH:mm
    handlerId: null,
    handlerName: '',
    approverId: null,
    approverName: '',
    approvalStatus: '待审批',
    remark: ''
  })
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  try {
    // 新增出库记录
    await assetOutRecordApi.createOutRecord(form)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    loadData(1)
  } catch (err) {
    ElMessage.error('保存失败：' + (err.response?.data?.message || err.message))
  }
}

// 加载实训室数据
const loadLabs = async () => {
  try {
    const res = await labInfoApi.getAllLabs()
    labs.value = res || []
  } catch (err) {
    ElMessage.error('加载实训室失败：' + (err.response?.data?.message || err.message))
  }
}

// 组件挂载时加载数据
onMounted(async () => {
  await fetchCurrentUser() // 确保在加载数据前获取当前用户信息
  await loadLabs() // 加载实训室数据
  loadData() // 加载出库记录列表数据
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>