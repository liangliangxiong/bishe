<template>
  <div class="user-manage-container">
    <el-card class="box-card" :body-style="{ padding: '0px' }">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">用户管理</span>
            <el-tag type="info" effect="plain" class="header-count">
              共 {{ total }} 条记录
            </el-tag>
          </div>
          <div class="header-operations">
            <el-input
              v-model="searchQuery"
              placeholder="搜索用户名/姓名"
              class="search-input"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon class="search-icon"><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleAdd" class="add-button">
              <el-icon><Plus /></el-icon>添加用户
            </el-button>
          </div>
        </div>
      </template>

      <!-- 用户列表表格 -->
      <div class="table-container">
        <el-table
          v-loading="loading"
          :data="userList"
          border
          stripe
          style="width: 100%"
          :header-cell-style="{
            background: '#f5f7fa',
            color: '#606266',
            fontWeight: 'bold'
          }"
        >
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="realName" label="姓名" min-width="120" />
          <el-table-column prop="roleName" label="角色" min-width="100" align="center">
            <template #default="{ row }">
              <el-tag
                :type="getRoleTagType(row.roleId)"
                effect="light"
                class="role-tag"
              >
                {{ row.roleName }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="手机号" min-width="130" align="center" />
          <el-table-column prop="createdAt" label="创建时间" min-width="180" align="center">
            <template #default="{ row }">
              <span class="time-text">{{ formatDate(row.createdAt) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250" fixed="right" align="center">
            <template #default="{ row }">
              <el-button-group class="operation-group">
                <el-button type="primary" @click="handleEdit(row)" :icon="Edit" size="small">
                  编辑
                </el-button>
                <el-button type="warning" @click="handleResetPwd(row)" :icon="Key" size="small">
                  重置密码
                </el-button>
                <el-button 
                  type="danger" 
                  @click="handleDelete(row)"
                  :icon="Delete"
                  v-if="row.roleId !== 1"
                  size="small"
                >
                  删除
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
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

      <!-- 添加/编辑用户对话框 -->
      <el-dialog
        :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
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
          <el-form-item label="用户名" prop="username">
            <el-input v-model="formData.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
            <el-input
              v-model="formData.password"
              type="text"
              placeholder="默认密码：123456"
              disabled
              class="disabled-input"
            >
              <template #append>
                <el-tooltip
                  content="新用户默认密码为：123456"
                  placement="top"
                >
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="formData.realName" placeholder="请输入姓名" clearable />
          </el-form-item>
          <el-form-item label="角色" prop="roleId">
            <el-select v-model="formData.roleId" placeholder="请选择角色" style="width: 100%" clearable>
              <el-option
                v-for="role in roleOptions"
                :key="role.value"
                :label="role.label"
                :value="role.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="formData.phone" placeholder="请输入手机号" clearable />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="loading">确 定</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { Search, Plus, Edit, Delete, Key, InfoFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserListService,
  addUserService,
  updateUserService,
  deleteUserService,
  resetUserPasswordService,
  getUserDetailService
} from '@/api/user'

// 数据定义
const loading = ref(false)
const userList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 表单数据
const formData = reactive({
  userId: '',
  username: '',
  password: '',
  realName: '',
  roleId: undefined,
  phone: ''
})

// 角色选项
const roleOptions = [
  { value: 1, label: '管理员' },
  { value: 2, label: '医生' },
  { value: 3, label: '家长' }
]

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    const { data } = await getUserListService({
      page: currentPage.value,
      pageSize: pageSize.value,
      query: searchQuery.value
    })
    userList.value = data.items
    total.value = data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 角色标签类型
const getRoleTagType = (roleId) => {
  switch (roleId) {
    case 1: return 'danger'
    case 2: return 'warning'
    case 3: return 'success'
    default: return 'info'
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getUserList()
}

// 添加用户
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.resetFields()
    Object.assign(formData, {
      userId: '',
      username: '',
      password: '123456', // 设置默认密码
      realName: '',
      roleId: undefined,
      phone: ''
    })
  })
}

// 编辑用户
const handleEdit = async (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  try {
    // 获取最新的用户数据
    const { data } = await getUserDetailService(row.userId)
    nextTick(() => {
      formRef.value?.resetFields()
      // 只保留需要编辑的字段
      Object.assign(formData, {
        userId: data.userId,
        username: data.username,
        realName: data.realName,
        roleId: data.roleId,
        phone: data.phone
      })
    })
  } catch (error) {
    ElMessage.error('获取用户信息失败')
    dialogVisible.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 创建一个新对象，只包含需要的字段
        const submitData = {
          userId: formData.userId,
          username: formData.username,
          realName: formData.realName,
          roleId: formData.roleId,
          phone: formData.phone
        }
        
        if (dialogType.value === 'add') {
          submitData.password = '123456' // 添加用户时设置默认密码
          await addUserService(submitData)
          ElMessage.success('添加成功（默认密码：123456）')
        } else {
          await updateUserService(submitData.userId, submitData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        getUserList()
      } catch (error) {
        const errorMsg = error.response?.data?.message || '操作失败'
        ElMessage.error(dialogType.value === 'add' ? `添加用户失败：${errorMsg}` : `更新用户失败：${errorMsg}`)
      }
    }
  })
}

// 删除用户
const handleDelete = (row) => {
  if (row.roleId === 1) {
    ElMessage.warning('不能删除超级管理员')
    return
  }
  
  ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUserService(row.userId)
      ElMessage.success('删除成功')
      // 如果当前页只有一条数据，且不是第一页，则跳转到上一页
      if (userList.value.length === 1 && currentPage.value > 1) {
        currentPage.value--
      }
      getUserList()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }).catch(() => {
    // 取消删除操作
  })
}

// 重置密码
const handleResetPwd = (row) => {
  ElMessageBox.confirm(
    '确定要重置该用户的密码吗？重置后的默认密码为：123456',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await resetUserPasswordService(row.userId)
      ElMessage.success('密码重置成功，新密码为：123456')
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '重置密码失败')
    }
  }).catch(() => {
    // 取消重置操作
  })
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  getUserList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getUserList()
}

// 页面加载时获取数据
onMounted(() => {
  getUserList()
})
</script>

<style lang="scss" scoped>
.user-manage-container {
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
      background-color: #fff;

      .header-left {
        display: flex;
        align-items: center;
        gap: 12px;

        .header-title {
          font-size: 18px;
          font-weight: 600;
          color: #303133;
        }

        .header-count {
          font-size: 13px;
        }
      }

      .header-operations {
        display: flex;
        gap: 16px;
        align-items: center;

        .search-input {
          width: 280px;
          transition: all 0.3s;

          &:focus-within {
            width: 320px;
          }

          .search-icon {
            color: #909399;
          }
        }

        .add-button {
          padding: 9px 16px;
        }
      }
    }
  }

  .table-container {
    padding: 20px;
    background-color: #fff;

    .role-tag {
      min-width: 65px;
      text-align: center;
    }

    .time-text {
      color: #606266;
      font-size: 13px;
    }

    .operation-group {
      .el-button {
        padding: 6px 12px;
        margin: 0;
      }
    }
  }

  .pagination-container {
    padding: 16px 20px;
    background-color: #fff;
    border-top: 1px solid #ebeef5;
    display: flex;
    justify-content: flex-end;
  }

  .dialog-form {
    padding: 20px 20px 0;

    :deep(.el-form-item) {
      margin-bottom: 22px;

      .el-form-item__label {
        font-weight: 500;
      }

      .el-input__wrapper,
      .el-select {
        width: 100%;
      }
    }
  }

  .dialog-footer {
    padding: 20px;
    text-align: right;
    border-top: 1px solid #ebeef5;
    margin: 0 -20px;

    .el-button {
      padding: 9px 20px;
      min-width: 100px;
    }
  }

  :deep(.el-card__body) {
    padding: 0;
  }

  :deep(.el-table) {
    border: 1px solid #ebeef5;
    border-radius: 4px;

    th {
      font-weight: 600;
      background-color: #f5f7fa !important;
      border-bottom: 1px solid #ebeef5;
      color: #606266;
      padding: 12px 0;
    }

    td {
      padding: 12px 0;
    }
  }

  :deep(.el-pagination) {
    button:not(:disabled) {
      background-color: #fff;
      color: #606266;
    }

    .el-pagination__sizes {
      margin-right: 16px;
    }
  }

  :deep(.el-dialog) {
    border-radius: 8px;
    overflow: hidden;

    .el-dialog__header {
      margin: 0;
      padding: 20px;
      border-bottom: 1px solid #ebeef5;

      .el-dialog__title {
        font-weight: 600;
        color: #303133;
      }
    }

    .el-dialog__body {
      padding: 0;
    }
  }
}

.disabled-input {
  :deep(.el-input__wrapper) {
    background-color: #f5f7fa;
    
    &.is-disabled {
      .el-input__inner {
        color: #606266;
        -webkit-text-fill-color: #606266;
      }
    }
  }
  
  :deep(.el-input-group__append) {
    background-color: #f5f7fa;
    border-left: 1px solid #dcdfe6;
    padding: 0 12px;
    color: #909399;
  }
}
</style> 