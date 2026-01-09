<template>
  <div>
    <!-- 卡片容器 -->
    <el-card shadow="never">
      <template #header>
          <div class="card-header">
            <span>实训室管理</span>
            <el-button v-if="isAdmin" type="primary" @click="handleAdd">新增实训室</el-button>
          </div>
        </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline>
        <el-form-item label="实训室名称">
          <el-input v-model="searchForm.labName" placeholder="请输入实训室名称" clearable />
        </el-form-item>

        <!-- 地理位置筛选 -->
        <el-form-item label="地理位置">
          <el-input v-model="searchForm.location" placeholder="请输入地理位置" clearable />
        </el-form-item>

        <!-- 状态筛选 -->
        <el-form-item label="实训室状态">
          <el-select
            v-model="searchForm.status"
            placeholder="全部状态"
            clearable
            style="width: 150px"
          >
            <el-option label="正常使用" value="正常使用" />
            <el-option label="维护中" value="维护中" />
            <el-option label="已停用" value="已停用" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="()=>loadData()">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" style="width: 100%; margin-top: 20px" v-loading="loading">
        <el-table-column prop="labId" label="实训室ID" width="100" />
        <el-table-column prop="labName" label="实训室名称" min-width="150" />
        <el-table-column prop="location" label="地理位置" width="150" />
        <el-table-column prop="capacity" label="容纳人数" width="100" />
        <el-table-column prop="managerName" label="管理员" width="120">
          <template #default="{ row }">
            {{ row.managerName ? row.managerName : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="contact" label="联系电话" width="120">
          <template #default="{ row }">
            {{ row.contact ? row.contact : '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="
                row.status === '正常使用'
                  ? 'success'
                  : row.status === '维护中'
                  ? 'warning'
                  : 'danger'
              "
            >
              {{ row.status }}
            </el-tag>
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
            <el-button v-if="isAdmin" size="small" type="danger" link @click="handleDelete(row.labId)">删除</el-button>
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
        <el-form-item label="实训室名称" prop="labName">
          <el-input v-model="form.labName" placeholder="请输入实训室名称" />
        </el-form-item>
        <el-form-item label="地理位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入地理位置" />
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input v-model.number="form.capacity" placeholder="请输入容纳人数" />
        </el-form-item>
        <el-form-item label="管理员姓名" prop="managerName">
          <el-input v-model="form.managerName" placeholder="请输入管理员姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="正常使用" value="正常使用" />
            <el-option label="维护中" value="维护中" />
            <el-option label="已停用" value="已停用" />
          </el-select>
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
import * as labInfoApi from '@/api/labInfo' // 实训室API

// 获取当前用户信息
const { currentUser, fetchCurrentUser } = useAuth()
const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')

// 分页 & 搜索
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({ 
  labName: '',
  location: '',
  status: ''
})
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 弹窗
const dialogVisible = ref(false)
const editMode = ref(false)
const form = reactive({
  labId: null,
  labName: '',
  location: '',
  capacity: 0,
  managerId: null,
  managerName: '',
  contact: '',
  status: '正常使用'
})

const formRef = ref()

// 计算弹窗标题
const dialogTitle = computed(() => (editMode.value ? '编辑实训室' : '新增实训室'))

// 表单校验规则
const rules = {
  labName: [{ required: true, message: '请输入实训室名称', trigger: 'blur' }, { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }],
  location: [{ required: true, message: '请输入地理位置', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容纳人数', trigger: 'blur' }, { type: 'number', message: '容纳人数必须为数字', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
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
      const res = await labInfoApi.getLabsPage(currentPage, pageSize.value, searchForm)
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
  searchForm.labName = ''
  searchForm.location = ''
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
  ElMessageBox.confirm('确定要删除该实训室吗？', '提示', { type: 'warning' }).then(async () => {
    await labInfoApi.deleteLab(id)
    ElMessage.success('删除成功')
    loadData()
  })
}

// 新增
const handleAdd = () => {
  editMode.value = false
  Object.assign(form, {
    labId: null,
    labName: '',
    location: '',
    capacity: 0,
    managerId: null,
    managerName: '',
    contact: '',
    status: '正常使用'
  })
  dialogVisible.value = true
}

// 编辑实训室
const handleEdit = (row) => {
  editMode.value = true
  Object.assign(form, { 
    labId: row.labId,
    labName: row.labName,
    location: row.location,
    capacity: row.capacity,
    managerId: row.managerId,
    managerName: row.managerName,
    contact: row.contact,
    status: row.status
  })
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  try {
    if (editMode.value) {
      // 更新实训室
      await labInfoApi.updateLab(form.labId, form)
      ElMessage.success('更新成功')
    } else {
      // 新增实训室
      await labInfoApi.createLab(form)
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
  loadData() // 加载实训室列表数据
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>