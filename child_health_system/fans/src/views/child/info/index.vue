<template>
  <div class="child-info-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">儿童信息管理</span>
            <el-tag v-if="userRole === 1" type="danger" effect="dark">管理员</el-tag>
            <el-tag v-else-if="userRole === 2" type="warning" effect="dark">医生</el-tag>
            <el-tag v-else type="success" effect="dark">家长</el-tag>
          </div>
          <el-button 
            v-if="userRole !== 2" 
            type="primary" 
            @click="handleAdd"
          >
            <el-icon><Plus /></el-icon>添加儿童
          </el-button>
        </div>
      </template>

      <!-- 儿童列表 -->
      <div class="table-container">
        <!-- 搜索栏 -->
        <div v-if="userRole !== 3" class="search-bar">
          <el-input
            v-model="searchQuery"
            placeholder="搜索儿童姓名"
            class="search-input"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon class="search-icon"><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>

        <el-table
          v-loading="loading"
          :data="childList"
          style="width: 100%"
          border
          stripe
          :header-cell-style="{
            background: '#f5f7fa',
            color: '#606266',
            fontWeight: 'bold'
          }"
        >
          <el-table-column prop="name" label="姓名" min-width="120">
            <template #default="{ row }">
              <span class="child-name">{{ row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="gender" label="性别" width="80" align="center">
            <template #default="{ row }">
              <el-tag
                :type="row.gender === 'MALE' ? 'info' : row.gender === 'FEMALE' ? 'danger' : 'warning'"
                effect="light"
                size="small"
              >
                {{ row.gender === 'MALE' ? '男' : row.gender === 'FEMALE' ? '女' : '未知' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="birthDate" label="出生日期" width="120" align="center">
            <template #default="{ row }">
              <span class="birth-date">{{ formatDate(row.birthDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="bloodType" label="血型" width="80" align="center">
            <template #default="{ row }">
              <el-tag
                v-if="row.bloodType"
                type="success"
                effect="plain"
                size="small"
              >
                {{ row.bloodType }}型
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column 
            v-if="userRole !== 3" 
            prop="parentName" 
            label="家长" 
            min-width="120" 
            align="center"
          >
            <template #default="{ row }">
              <el-tooltip
                v-if="row.parentName"
                :content="'ID: ' + row.parentId"
                placement="top"
              >
                <span class="parent-name">{{ row.parentName }}</span>
              </el-tooltip>
              <span v-else class="no-parent">未指定</span>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
            <template #default="{ row }">
              <span class="time-text">{{ formatDateTime(row.createdAt) }}</span>
            </template>
          </el-table-column>
          <el-table-column 
            v-if="userRole !== 2" 
            label="操作" 
            width="200" 
            fixed="right" 
            align="center"
          >
            <template #default="{ row }">
              <el-button-group v-if="canManageChild(row)">
                <el-button type="primary" @click="handleEdit(row)" :icon="Edit" size="small">
                  编辑
                </el-button>
                <el-button type="danger" @click="handleDelete(row)" :icon="Delete" size="small">
                  删除
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div v-if="userRole !== 3" class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
          />
        </div>
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '添加儿童' : '编辑儿童'"
      v-model="dialogVisible"
      width="500px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
        class="dialog-form"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio label="MALE">男</el-radio>
            <el-radio label="FEMALE">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker
            v-model="formData.birthDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="血型" prop="bloodType">
          <el-select v-model="formData.bloodType" placeholder="请选择血型" clearable style="width: 100%">
            <el-option label="A型" value="A" />
            <el-option label="B型" value="B" />
            <el-option label="AB型" value="AB" />
            <el-option label="O型" value="O" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="userRole === 1" label="家长" prop="parentId">
          <el-select 
            v-model="formData.parentId" 
            placeholder="请选择家长" 
            :disabled="dialogType === 'edit'"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="parent in parentList"
              :key="parent.userId"
              :label="parent.realName || parent.username"
              :value="parent.userId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确 定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete, Plus, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTokenStore } from '@/stores/token'
import {
  getChildListService,
  getMyChildrenService,
  addChildService,
  updateChildService,
  deleteChildService
} from '@/api/child'
import { getUserListService } from '@/api/user'

// 数据定义
const loading = ref(false)
const submitting = ref(false)
const childList = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const userStore = useTokenStore()
const userRole = ref(userStore.roleId)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')
const parentList = ref([])

// 表单数据
const formData = ref({
  name: '',
  gender: 'MALE',
  birthDate: '',
  bloodType: '',
  parentId: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthDate: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  parentId: [
    { required: userRole.value === 1, message: '请选择家长', trigger: 'change' }
  ]
}

// 获取儿童列表
const getChildList = async () => {
  loading.value = true
  try {
    if (userRole.value === 3) {
      // 家长只能查看自己的儿童
      const { data } = await getMyChildrenService()
      childList.value = data
    } else {
      // 管理员和医生可以查看所有儿童
      const { data } = await getChildListService({
        page: currentPage.value,
        pageSize: pageSize.value,
        query: searchQuery.value
      })
      childList.value = data.items
      total.value = data.total
    }
  } catch (error) {
    ElMessage.error('获取儿童列表失败')
  } finally {
    loading.value = false
  }
}

// 获取家长列表
const getParentList = async () => {
  try {
    const { data } = await getUserListService({
      roleId: 3  // 只获取家长角色的用户
    })
    parentList.value = data.items
  } catch (error) {
    ElMessage.error('获取家长列表失败')
  }
}

// 判断是否可以管理该儿童
const canManageChild = (child) => {
  if (userRole.value === 1) return true  // 管理员可以管理所有儿童
  if (userRole.value === 2) return false // 医生不能管理儿童
  // 家长只能管理自己的儿童
  return child.parentId === userStore.userInfo?.userId
}

// 添加儿童
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  formData.value = {
    name: '',
    gender: 'MALE',
    birthDate: '',
    bloodType: '',
    parentId: userRole.value === 3 ? userStore.userInfo?.userId : ''
  }
  if (userRole.value === 1) {
    getParentList()
  }
}

// 编辑儿童
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  formData.value = {
    childId: row.childId,
    name: row.name,
    gender: row.gender,
    birthDate: row.birthDate,
    bloodType: row.bloodType,
    parentId: row.parentId
  }
}

// 删除儿童
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该儿童信息吗？此操作不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteChildService(row.childId)
      ElMessage.success('删除成功')
      getChildList()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (dialogType.value === 'add') {
          await addChildService(formData.value)
          ElMessage.success('添加成功')
        } else {
          await updateChildService(formData.value.childId, formData.value)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        getChildList()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 搜索
const handleSearch = () => {
  if (userRole.value !== 3) {
    currentPage.value = 1
    getChildList()
  }
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  getChildList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getChildList()
}

// 日期格式化
const formatDate = (date) => {
  if (!date) return '-'
  return date.split('T')[0]
}

// 日期时间格式化
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 禁用未来日期
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 页面加载时获取数据
onMounted(() => {
  getChildList()
})
</script>

<style lang="scss" scoped>
.child-info-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);

  .box-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
      padding: 16px 20px;
      border-bottom: 1px solid #ebeef5;

      .header-left {
        display: flex;
        align-items: center;
        gap: 12px;

        .header-title {
          font-size: 18px;
          font-weight: 600;
          color: #303133;
        }
      }
    }
  }

  .table-container {
    padding: 20px;

    .search-bar {
      margin-bottom: 20px;
      display: flex;
      gap: 16px;

      .search-input {
        width: 300px;
      }
    }

    .child-name {
      font-weight: 500;
      color: #303133;
    }

    .birth-date {
      color: #606266;
    }

    .parent-name {
      color: #409EFF;
      cursor: pointer;
    }

    .no-parent {
      color: #909399;
      font-style: italic;
    }

    .time-text {
      color: #606266;
      font-size: 13px;
    }
  }

  :deep(.el-card__body) {
    padding: 0;
  }

  .dialog-form {
    padding: 20px 20px 0;

    :deep(.el-form-item) {
      margin-bottom: 22px;
    }
  }

  .dialog-footer {
    padding: 10px 20px 20px;
    text-align: right;
  }

  .pagination-container {
    margin-top: 20px;
    padding: 10px 20px;
    display: flex;
    justify-content: flex-end;
    background-color: #fff;
    border-top: 1px solid #ebeef5;
  }
}
</style> 