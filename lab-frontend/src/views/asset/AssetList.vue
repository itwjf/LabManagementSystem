<template>
  <div>
    <!-- 卡片容器 -->
    <el-card shadow="never">
      <template #header>
          <div class="card-header">
            <span>固定资产列表</span>
            <el-button v-if="isAdmin" type="primary" @click="handleAdd">新增资产</el-button>
          </div>
        </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline>
        <el-form-item label="资产名称">
          <el-input v-model="searchForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>

        <!-- 资产类型筛选 -->
        <el-form-item label="资产类型">
          <el-select
            v-model="searchForm.typeId"
            placeholder="全部类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="type in assetTypes"
              :key="type.typeId"
              :label="type.typeName"
              :value="type.typeId"
            />
          </el-select>
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

        <!-- 状态筛选 -->
        <el-form-item label="资产状态">
          <el-select
            v-model="searchForm.status"
            placeholder="全部状态"
            clearable
            style="width: 150px"
          >
            <el-option label="正常使用" value="正常使用" />
            <el-option label="维修中" value="维修中" />
            <el-option label="已报废" value="已报废" />
            <el-option label="出库待处置" value="出库待处置" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="()=>loadData()">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" style="width: 100%; margin-top: 20px" v-loading="loading">
        <el-table-column prop="assetId" label="资产编号" width="120" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" />
        <el-table-column prop="typeName" label="资产类型" width="120">
          <template #default="{ row }">
            {{ row.typeName ? row.typeName : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="specModel" label="规格型号" width="150" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">
            {{ row.unitPrice ? '¥' + row.unitPrice : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalQuantity" label="总数量" width="100" />
        <el-table-column prop="availableQuantity" label="可用数量" width="100" />
        <el-table-column prop="totalValue" label="总价值" width="120">
          <template #default="{ row }">
            {{ row.totalValue ? '¥' + row.totalValue : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="
                row.status === '正常使用'
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
        
        <el-table-column prop="labName" label="所属实训室" width="120">
          <template #default="{ row }">
            {{ row.labName ? row.labName : '—' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="purchaseDate" label="购置日期" width="120">
          <template #default="{ row }">
            {{ row.purchaseDate ? row.purchaseDate : '—' }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <!-- 仅管理员可见操作 编辑、删除-->    
            <el-button v-if="isAdmin" size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="isAdmin" size="small" type="danger" link @click="handleDelete(row.assetId)">删除</el-button>
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
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="form.assetName" placeholder="请输入资产名称" />
        </el-form-item>
        <el-form-item label="资产类型" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择资产类型">
            <el-option
              v-for="type in assetTypes"
              :key="type.typeId"
              :label="type.typeName"
              :value="type.typeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规格型号" prop="specModel">
          <el-input v-model="form.specModel" placeholder="请输入规格型号" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input v-model.number="form.unitPrice" placeholder="请输入单价" />
        </el-form-item>
        <el-form-item label="总数量" prop="totalQuantity">
          <el-input v-model.number="form.totalQuantity" placeholder="请输入总数量" />
        </el-form-item>
        <el-form-item label="可用数量" prop="availableQuantity">
          <el-input v-model.number="form.availableQuantity" placeholder="请输入可用数量" />
        </el-form-item>
        <el-form-item label="所属实训室" prop="labId">
          <el-select v-model="form.labId" placeholder="请选择所属实训室">
            <el-option
              v-for="lab in labs"
              :key="lab.labId"
              :label="lab.labName"
              :value="lab.labId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="正常使用" value="正常使用" />
            <el-option label="维修中" value="维修中" />
            <el-option label="已报废" value="已报废" />
            <el-option label="出库待处置" value="出库待处置" />
          </el-select>
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
        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="form.supplier" placeholder="请输入供应商" />
        </el-form-item>
        <el-form-item label="采购人" prop="purchaserName">
          <el-input v-model="form.purchaserName" placeholder="请输入采购人" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuth } from '@/composables/useAuth' // 引入权限控制
import * as assetApi from '@/api/asset' // 资产API
import * as assetTypeApi from '@/api/assetType' // 资产类型API
import * as labInfoApi from '@/api/labInfo' // 实训室API

// 获取当前用户信息
const { currentUser, fetchCurrentUser } = useAuth()
const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')

// 分页 & 搜索
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({ 
  assetName: '',
  typeId: null,
  labId: null,
  status: ''
})
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 资产类型和实训室数据
const assetTypes = ref([])
const labs = ref([])

// 弹窗
const dialogVisible = ref(false)
const editMode = ref(false)
const form = reactive({
  assetId: null,
  assetName: '',
  typeId: null,
  specModel: '',
  unit: '',
  unitPrice: null,
  totalQuantity: 0,
  availableQuantity: 0,
  totalValue: null,
  purchaseDate: null,
  purchaserId: null,
  purchaserName: '',
  supplier: '',
  status: '正常使用',
  labId: null
})

const formRef = ref()

// 计算弹窗标题
const dialogTitle = computed(() => (editMode.value ? '编辑资产' : '新增资产'))

// 表单校验规则
const rules = {
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }, { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }],
  typeId: [{ required: true, message: '请选择资产类型', trigger: 'change' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }, { type: 'number', message: '单价必须为数字', trigger: 'blur' }],
  totalQuantity: [{ required: true, message: '请输入总数量', trigger: 'blur' }, { type: 'number', message: '总数量必须为数字', trigger: 'blur' }],
  availableQuantity: [{ required: true, message: '请输入可用数量', trigger: 'blur' }, { type: 'number', message: '可用数量必须为数字', trigger: 'blur' }],
  labId: [{ required: true, message: '请选择所属实训室', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 加载数据
const loadData = async (currentPage = page.value) => {
  // 防御性处理（防止传入 event）
  if (typeof currentPage !== 'number') currentPage = page.value

  loading.value = true
  try {
      const res = await assetApi.getAssetsPage(currentPage, pageSize.value, searchForm)
      // 响应拦截器已处理Result结构，直接使用res.records
      console.log('获取到的资产数据:', res?.records || [])
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
  searchForm.assetName = ''
  searchForm.typeId = null
  searchForm.labId = null
  searchForm.status = ''
  loadData(1)
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  loadData(1)
}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该资产吗？', '提示', { type: 'warning' }).then(async () => {
    await assetApi.deleteAsset(id)
    ElMessage.success('删除成功')
    loadData()
  })
}

// 新增
const handleAdd = () => {
  editMode.value = false
  Object.assign(form, {
    assetId: null,
    assetName: '',
    typeId: null,
    specModel: '',
    unit: '',
    unitPrice: null,
    totalQuantity: 0,
    availableQuantity: 0,
    totalValue: null,
    purchaseDate: null,
    purchaserId: null,
    purchaserName: '',
    supplier: '',
    status: '正常使用',
    labId: null
  })
  dialogVisible.value = true
}

// 编辑资产
const handleEdit = (row) => {
  editMode.value = true
  Object.assign(form, { 
    assetId: row.assetId,
    assetName: row.assetName,
    typeId: row.typeId,
    specModel: row.specModel,
    unit: row.unit,
    unitPrice: row.unitPrice,
    totalQuantity: row.totalQuantity,
    availableQuantity: row.availableQuantity,
    totalValue: row.totalValue,
    purchaseDate: row.purchaseDate,
    purchaserId: row.purchaserId,
    purchaserName: row.purchaserName,
    supplier: row.supplier,
    status: row.status,
    labId: row.labId
  })
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  
  // 计算总价值
  if (form.unitPrice && form.totalQuantity) {
    form.totalValue = form.unitPrice * form.totalQuantity
  }
  
  try {
    if (editMode.value) {
      // 更新资产
      await assetApi.updateAsset(form.assetId, form)
      ElMessage.success('更新成功')
    } else {
      // 新增资产
      await assetApi.createAsset(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData(1)
  } catch (err) {
    ElMessage.error('保存失败：' + (err.response?.data?.message || err.message))
  }
}

// 加载资产类型和实训室数据
const loadAssetTypes = async () => {
  try {
    const res = await assetTypeApi.getAllAssetTypes()
    assetTypes.value = res || []
  } catch (err) {
    ElMessage.error('加载资产类型失败：' + (err.response?.data?.message || err.message))
  }
}

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
  await Promise.all([loadAssetTypes(), loadLabs()]) // 并行加载资产类型和实训室数据
  loadData() // 加载资产列表数据
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>