<template>
  <div>
    <!-- 卡片容器 -->
    <el-card shadow="never">
      <template #header>
          <div class="card-header">
            <span>资产类型管理</span>
            <el-button v-if="isAdmin" type="primary" @click="handleAdd">新增类型</el-button>
          </div>
        </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline>
        <el-form-item label="类型名称">
          <el-input v-model="searchForm.typeName" placeholder="请输入类型名称" clearable />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="()=>loadData()">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" style="width: 100%; margin-top: 20px" v-loading="loading">
        <el-table-column prop="typeId" label="类型ID" width="100" />
        <el-table-column prop="typeName" label="类型名称" min-width="150" />
        <el-table-column prop="description" label="类型描述" min-width="200">
          <template #default="{ row }">
            {{ row.description ? row.description : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ row.createTime ? formatDateTime(row.createTime) : '—' }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <!-- 仅管理员可见操作 编辑、删除-->    
            <el-button v-if="isAdmin" size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="isAdmin" size="small" type="danger" link @click="handleDelete(row.typeId)">删除</el-button>
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
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="类型描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入类型描述" />
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
import * as assetTypeApi from '@/api/assetType' // 资产类型API

// 获取当前用户信息
const { currentUser, fetchCurrentUser } = useAuth()
const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')

// 分页 & 搜索
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({ 
  typeName: ''
})
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 弹窗
const dialogVisible = ref(false)
const editMode = ref(false)
const form = reactive({
  typeId: null,
  typeName: '',
  description: ''
})

const formRef = ref()

// 计算弹窗标题
const dialogTitle = computed(() => (editMode.value ? '编辑资产类型' : '新增资产类型'))

// 表单校验规则
const rules = {
  typeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }, { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }]
}

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
      const res = await assetTypeApi.getAssetTypesPage(currentPage, pageSize.value, searchForm.typeName)
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
  searchForm.typeName = ''
  loadData(1)
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  loadData(1)
}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该资产类型吗？', '提示', { type: 'warning' }).then(async () => {
    await assetTypeApi.deleteAssetType(id)
    ElMessage.success('删除成功')
    loadData()
  })
}

// 新增
const handleAdd = () => {
  editMode.value = false
  Object.assign(form, {
    typeId: null,
    typeName: '',
    description: ''
  })
  dialogVisible.value = true
}

// 编辑资产类型
const handleEdit = (row) => {
  editMode.value = true
  Object.assign(form, { 
    typeId: row.typeId,
    typeName: row.typeName,
    description: row.description
  })
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  try {
    if (editMode.value) {
      // 更新资产类型
      await assetTypeApi.updateAssetType(form.typeId, form)
      ElMessage.success('更新成功')
    } else {
      // 新增资产类型
      await assetTypeApi.createAssetType(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData(1)
  } catch (err) {
    ElMessage.error('保存失败：' + (err.response?.data?.message || err.message))
  }
}

// 组件挂载时加载数据
onMounted(async () => {
  await fetchCurrentUser() // 确保在加载数据前获取当前用户信息
  loadData() // 加载资产类型列表数据
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>