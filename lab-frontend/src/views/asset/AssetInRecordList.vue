<template>
  <div>
    <!-- 卡片容器 -->
    <el-card shadow="never">
      <template #header>
          <div class="card-header">
            <span>资产入库记录</span>
            <el-button v-if="isAdmin" type="primary" @click="handleAdd">新增入库</el-button>
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

        <!-- 采购人筛选 -->
        <el-form-item label="采购人">
          <el-input v-model="searchForm.purchaserName" placeholder="请输入采购人姓名" clearable />
        </el-form-item>

        <!-- 入库日期范围 -->
        <el-form-item label="入库日期">
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
        <el-table-column prop="quantity" label="入库数量" width="100" />
        <el-table-column prop="unitPrice" label="入库单价" width="100">
          <template #default="{ row }">
            {{ row.unitPrice ? '¥' + row.unitPrice : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="入库总金额" width="120">
          <template #default="{ row }">
            {{ row.totalAmount ? '¥' + row.totalAmount : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="购置日期" width="120">
          <template #default="{ row }">
            {{ row.purchaseDate ? row.purchaseDate : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="purchaserName" label="采购人" width="120">
          <template #default="{ row }">
            {{ row.purchaserName ? row.purchaserName : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="经办人" width="120">
          <template #default="{ row }">
            {{ row.handlerName ? row.handlerName : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="inDate" label="入库日期" width="180">
          <template #default="{ row }">
            {{ row.inDate ? formatDateTime(row.inDate) : '—' }}
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

    <!-- 弹窗：新增入库记录 -->
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
        <el-form-item label="入库数量" prop="quantity">
          <el-input v-model.number="form.quantity" placeholder="请输入入库数量" />
        </el-form-item>
        <el-form-item label="入库单价" prop="unitPrice">
          <el-input v-model.number="form.unitPrice" placeholder="请输入入库单价" />
        </el-form-item>
        <el-form-item label="购置日期" prop="purchaseDate">
          <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="采购人ID" prop="purchaserId">
          <el-input v-model="form.purchaserId" placeholder="请输入采购人ID" />
        </el-form-item>
        <el-form-item label="采购人姓名" prop="purchaserName">
          <el-input v-model="form.purchaserName" placeholder="请输入采购人姓名" />
        </el-form-item>
        <el-form-item label="经办人ID" prop="handlerId">
          <el-input v-model="form.handlerId" placeholder="请输入经办人ID" />
        </el-form-item>
        <el-form-item label="经办人姓名" prop="handlerName">
          <el-input v-model="form.handlerName" placeholder="请输入经办人姓名" />
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
import * as assetInRecordApi from '@/api/assetInRecord' // 资产入库记录API
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
  purchaserName: '',
  inDateStart: '',
  inDateEnd: ''
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
  unitPrice: null,
  totalAmount: null,
  purchaseDate: null,
  purchaserId: null,
  purchaserName: '',
  handlerId: null,
  handlerName: '',
  remark: ''
})

const formRef = ref()

// 计算弹窗标题
const dialogTitle = computed(() => (editMode.value ? '编辑入库记录' : '新增入库记录'))

// 表单校验规则
const rules = {
  assetId: [{ required: true, message: '请输入资产ID', trigger: 'blur' }],
  labId: [{ required: true, message: '请选择所属实训室', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }, { type: 'number', message: '入库数量必须为数字', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入入库单价', trigger: 'blur' }, { type: 'number', message: '入库单价必须为数字', trigger: 'blur' }],
  purchaseDate: [{ required: true, message: '请选择购置日期', trigger: 'change' }],
  purchaserName: [{ required: true, message: '请输入采购人姓名', trigger: 'blur' }],
  handlerName: [{ required: true, message: '请输入经办人姓名', trigger: 'blur' }]
}

// 监听日期范围变化
watch(dateRange, (newValue) => {
  if (newValue && newValue.length === 2) {
    searchForm.inDateStart = newValue[0]
    searchForm.inDateEnd = newValue[1]
  } else {
    searchForm.inDateStart = ''
    searchForm.inDateEnd = ''
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
    const res = await assetInRecordApi.getInRecordsPage(currentPage, pageSize.value, searchForm)
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
  searchForm.purchaserName = ''
  searchForm.inDateStart = ''
  searchForm.inDateEnd = ''
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
  ElMessageBox.confirm('确定要删除该入库记录吗？', '提示', { type: 'warning' }).then(async () => {
    await assetInRecordApi.deleteInRecord(id)
    ElMessage.success('删除成功')
    loadData()
  })
}

// 新增
const handleAdd = () => {
  editMode.value = false
  Object.assign(form, {
    recordId: null,
    labId: null,
    assetId: null,
    quantity: 0,
    unitPrice: null,
    totalAmount: null,
    purchaseDate: null,
    purchaserId: null,
    purchaserName: '',
    handlerId: null,
    handlerName: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  
  // 计算总金额
  if (form.unitPrice && form.quantity) {
    form.totalAmount = form.unitPrice * form.quantity
  }
  
  try {
    // 新增入库记录
    await assetInRecordApi.createInRecord(form)
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
  loadData() // 加载入库记录列表数据
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>