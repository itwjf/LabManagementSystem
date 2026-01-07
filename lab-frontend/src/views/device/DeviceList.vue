<template>
  <div>
    <!-- 卡片容器 -->
    <el-card shadow="never">
      <template #header>
          <div class="card-header">
            <span>设备列表</span>
            <el-button v-if="isAdmin" type="primary" @click="handleAdd">新增设备</el-button>
          </div>
        </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline>
        <el-form-item label="设备名称">
          <el-input v-model="searchForm.name" placeholder="请输入设备名称" clearable />
        </el-form-item>

        <!-- 设备类别筛选 -->
        <el-form-item label="设备类别">
          <el-select
            v-model="searchForm.category"
            placeholder="全部类别"
            clearable
            style="width: 150px"
          >
            <el-option label="计算机" value="计算机" />
            <el-option label="示波器" value="示波器" />
            <el-option label="万用表" value="万用表" />
            <el-option label="电源" value="电源" />
            <!-- 可根据实际需求扩展 -->
          </el-select>
        </el-form-item>

        
        <el-form-item>
          <el-button type="primary" @click="()=>loadData()">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          
          <el-button type="success" @click="exportExcel" :loading="exportLoading">导出 Excel</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" style="width: 100%; margin-top: 20px" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="设备名称" min-width="150" />
        <el-table-column prop="category" label="设备类别" min-width="120" />
        <el-table-column prop="location" label="存放位置" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="
                row.status === '可用'
                  ? 'success'
                  : row.status === '维修中'
                  ? 'warning'
                  : 'danger'
              "
            >
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="purchaseDate" label="购入日期" width="120">
          <template #default="{ row }">
            {{ row.purchaseDate ? row.purchaseDate : '—' }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button
              v-if="row.status === '可用'"
              size="small"
              type="primary"
              @click="openBorrowDialog(row)"
            >
              借用
            </el-button>
            <!-- 仅管理员可见操作 编辑、删除-->
            <el-button v-if="isAdmin" size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="isAdmin" size="small" type="danger" link @click="handleDelete(row.id)">删除</el-button>
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

    <!-- 弹窗：新增/编辑 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备类别" prop="category">
          <el-input v-model="form.category" placeholder="如：计算机、电子器件" />
        </el-form-item>
        <el-form-item label="存放位置" prop="location">
          <el-input v-model="form.location" placeholder="如：302实验室A区" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="可用" value="可用" />
            <el-option label="维修中" value="维修中" />
            <el-option label="报废" value="报废" />
            <el-option label="已借出" value="已借出" />
          </el-select>
        </el-form-item>
        <el-form-item label="购入日期" prop="purchaseDate">
          <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!--  设备借用弹窗 -->
    <el-dialog v-model="borrowDialogVisible" title="设备借用登记" width="500px">
      <el-form :model="borrowForm" :rules="rules" ref="borrowFormRef" label-width="100px">
        <el-form-item label="设备名称">
          {{ currentDevice?.name }}
        </el-form-item>
        
        <el-form-item label="预计归还时间" prop="expectedReturnTime">
          <el-date-picker
            v-model="borrowForm.expectedReturnTime"
            type="datetime"
            placeholder="选择预计归还时间"
            value-format="YYYY-MM-DD HH:mm"
            format="YYYY-MM-DD HH:mm"
            editable
            clearable
          />
        </el-form-item>
        
        <el-form-item label="借用用途" prop="purpose">
          <el-input v-model="borrowForm.purpose" type="textarea" :rows="2" />
        </el-form-item>
        
        <el-form-item label="设备状态" prop="deviceConditionOnBorrow">
          <el-radio-group v-model="borrowForm.deviceConditionOnBorrow">
            <el-radio label="正常">正常</el-radio>
            <el-radio label="轻微瑕疵">轻微瑕疵</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBorrow">确认借用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
// 导入 Excel 相关库
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'
import { useAuth } from '@/composables/useAuth' // 引入权限控制

// 获取当前用户信息
const { currentUser, fetchCurrentUser } = useAuth()
const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')

// 分页 & 搜索
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({ 
  name: '' ,
  category: '' // 新增设备类别筛选
})
const loading = ref(false)

const exportLoading = ref(false) // 导出按钮 loading

// 表格数据
const tableData = ref([])

// 弹窗
const dialogVisible = ref(false)
const editMode = ref(false)
const form = reactive({
  id: null,
  name: '',
  category: '',
  location: '',
  status: '可用',
  purchaseDate: null // 注意：前端用 purchaseDate，后端 Java 用 purchaseDate（驼峰）
})

const formRef = ref()

// 计算弹窗标题
const dialogTitle = computed(() => (editMode.value ? '编辑设备' : '新增设备'))

// 表单校验规则
const rules = {
  //新增设备弹窗规则
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }, // 名称必填
         { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  category: [{ required: true, message: '请输入设备类别', trigger: 'blur' }, // 类别必填
            { min: 2, max: 50, message: '类别名称过长', trigger: 'blur' }
  ],
  location: [{ required: true, message: '请输入存放位置', trigger: 'blur' }, // 存放位置必填
            { pattern: /\S/, message: '不能只输入空格', trigger: 'blur' } 
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }], // 状态必填
  purchaseDate: [ // 可选字段，但如果填写则校验
    {
      validator: (rule, value, callback) => {
        if (value && new Date(value) > new Date()) {
          callback(new Error('购入日期不能是未来日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  //借用设备弹窗规则
  expectedReturnTime: [{ required: true, message: '请选择预计归还时间', trigger: 'blur' }],
  purpose: [{ required: true, message: '请输入借用用途', trigger: 'blur' }]
}

// 加载数据
const loadData = async (currentPage = page.value) => {

  // 防御性处理（防止传入 event）
  if (typeof currentPage !== 'number') currentPage = page.value

  loading.value = true
  //建议未来将设备相关 API 也封装到 src/api/device.js
  //这样组件里就不用直接写 axios.get(...)，便于后期替换或 mock。
  try {
    const res = await axios.get('/api/devices/page', {
      params: {
        page: currentPage,
        size: pageSize.value,
        name: searchForm.name || undefined,
        category: searchForm.category || undefined // 传递设备类别参数
      }
    })
    // MyBatis Plus 返回的是 records
    // res 就是 IPage 对象，不再有 .data.data
    tableData.value = res.records || []
    total.value = res.total || 0
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
  searchForm.name = ''
  searchForm.category = '' // 重置设备类别筛选
  loadData(1)
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  loadData(1)
}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该设备吗？', '提示', { type: 'warning' }).then(async () => {
    await axios.delete(`/api/devices/${id}`)
    ElMessage.success('删除成功')
    loadData()
  })
}

// 新增
const handleAdd = () => {
  editMode.value = false
  Object.assign(form, {
    id: null,
    name: '',
    category: '',
    location: '',
    status: '可用',
    purchaseDate: null
  })
  dialogVisible.value = true
}

// 编辑设备
const handleEdit = (row) => {
  editMode.value = true
  Object.assign(form, { 
    id: row.id,
    name: row.name,
    category: row.category,
    location: row.location,
    status: row.status,
    purchaseDate: row.purchaseDate // 注意：如果后端返回的是字符串 '2024-05-15'，el-date-picker 能识别
  })
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  try {
    if (editMode.value) {
      // 更新设备
      await axios.put(`/api/devices/${form.id}`, form)
      ElMessage.success('更新成功')
      
    } else {
      // 新增设备
      await axios.post('/api/devices', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData(1)
  } catch (err) {
    ElMessage.error('保存失败：' + (err.response?.data?.message || err.message))
  }
}

// 导出 Excel（按当前筛选条件）
const exportExcel = async () => {
  exportLoading.value = true
  try {
    // 调用后端导出接口（传当前搜索条件）
    const res = await axios.get('/api/devices/export', {
      params: {
        name: searchForm.name || undefined,
        category: searchForm.category || undefined
      },
      responseType: 'blob' //  关键：接收二进制数据
    })

    // 创建文件并下载
    // 注意：导出接口返回 blob，不会触发 JSON 拦截器，所以 res.data 是原始 Blob
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    saveAs(blob, `设备清单_${new Date().getTime()}.xlsx`)
    
    ElMessage.success('导出成功')
  } catch (err) {
    console.error('导出失败:', err)
    ElMessage.error('导出失败：' + (err.response?.data?.message || '网络错误'))
  } finally {
    exportLoading.value = false
  }
}

//定义借用设备弹窗和表单
const borrowDialogVisible = ref(false)
const borrowFormRef = ref()
const currentDevice = ref(null)

const borrowForm = reactive({
  deviceId: null,
  expectedReturnTime: '',
  purpose: '',
  deviceConditionOnBorrow: '正常'
})


import { borrowDevice } from '@/api/deviceBorrow'

//打开借用弹窗/提交 方法
const openBorrowDialog = (device) => {
  currentDevice.value = device
  borrowForm.deviceId = device.id
  // 默认 7 天后归还
  const now = new Date()
  now.setDate(now.getDate() + 7)
  borrowForm.expectedReturnTime = now.toISOString().slice(0, 16) // "2026-01-13T15:30"
  borrowForm.purpose = ''
  borrowForm.deviceConditionOnBorrow = '正常'
  borrowDialogVisible.value = true
}

const submitBorrow = async () => {
  await borrowFormRef.value.validate()
  
  // 转换时间为完整 ISO（带秒和 Z）
  const timeStr = new Date(borrowForm.expectedReturnTime).toISOString()
  
  await borrowDevice({
    ...borrowForm,
    expectedReturnTime: timeStr
  })
  
  ElMessage.success('借用申请已提交')
  borrowDialogVisible.value = false
  // 可选：刷新设备列表
  loadData() 
}


// 初始加载
fetchCurrentUser() // 确保在加载设备数据前获取当前用户信息
loadData()
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>