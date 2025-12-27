<template>
  <div>
    <!-- 卡片容器 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>设备列表</span>
          <el-button type="primary" @click="handleAdd">新增设备</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline>
        <el-form-item label="设备名称">
          <el-input v-model="searchForm.name" placeholder="请输入设备名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="()=>loadData()">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
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
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row.id)">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 分页 & 搜索
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({ name: '' })
const loading = ref(false)

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
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  category: [{ required: true, message: '请输入设备类别', trigger: 'blur' }],
  location: [{ required: true, message: '请输入存放位置', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 加载数据
const loadData = async (currentPage = page.value) => {
  loading.value = true
  try {
    const res = await axios.get('/api/devices/page', {
      params: {
        page: currentPage,
        size: pageSize.value,
        name: searchForm.name || undefined
      }
    })
    // MyBatis Plus 返回的是 records
    tableData.value = res.data.data.records || []
    total.value = res.data.data.total || 0
    page.value = currentPage
  } catch (err) {
    ElMessage.error('加载失败：' + (err.response?.data?.message || err.message))
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
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

// 编辑（预留，可后续实现）
const handleEdit = (row) => {
  editMode.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  try {
    if (editMode.value) {
      // TODO: 实现更新接口（如果需要）
      ElMessage.warning('编辑功能暂未实现')
    } else {
      await axios.post('/api/devices', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData(1)
  } catch (err) {
    ElMessage.error('保存失败：' + (err.response?.data?.message || err.message))
  }
}

// 初始加载
loadData()
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>