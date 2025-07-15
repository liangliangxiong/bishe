<template>
  <el-card class="user-info-container">
    <template #header>
      <div class="header">
        <span>基本资料</span>
        <el-button type="primary" @click="handleEdit" :icon="Edit">
          {{ isEdit ? '保存' : '编辑' }}
        </el-button>
      </div>
    </template>
    
    <el-form 
      :model="userForm" 
      :rules="rules"
      ref="formRef"
      label-width="120px"
      class="user-form"
    >
      <el-form-item label="用户ID">
        <el-input v-model="userForm.userId" disabled></el-input>
      </el-form-item>
      
      <el-form-item label="登录名称">
        <el-input v-model="userForm.username" disabled></el-input>
      </el-form-item>
      
      <el-form-item label="角色名称">
        <el-input v-model="userForm.roleName" disabled></el-input>
      </el-form-item>
      
      <el-form-item label="真实姓名" prop="realName">
        <el-input 
          v-model="userForm.realName" 
          :disabled="!isEdit"
          maxlength="10"
          clearable
        ></el-input>
      </el-form-item>
      
      <el-form-item label="联系电话" prop="phone">
        <el-input 
          v-model="userForm.phone" 
          :disabled="!isEdit"
          maxlength="11"
          clearable
        ></el-input>
      </el-form-item>
      
      <el-form-item v-if="isEdit">
        <el-button type="primary" @click="updateProfile" :loading="loading">确认更新</el-button>
        <el-button @click="cancelEdit">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useTokenStore } from '@/stores/token'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { getUserProfileService, updateUserProfileService } from '@/api/user'

const tokenStore = useTokenStore()
const formRef = ref(null)
const isEdit = ref(false)
const loading = ref(false)

// 表单数据
const userForm = ref({
  userId: '',
  username: '',
  roleName: '',
  realName: '',
  phone: ''
})

// 保存原始数据用于取消编辑
const originalForm = ref({})

// 表单校验规则
const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 处理编辑按钮
const handleEdit = () => {
  if (isEdit.value) {
    updateProfile()
  } else {
    isEdit.value = true
    // 保存原始数据
    originalForm.value = { ...userForm.value }
  }
}

// 取消编辑
const cancelEdit = () => {
  isEdit.value = false
  // 恢复原始数据
  userForm.value = { ...originalForm.value }
  formRef.value?.clearValidate()
}

// 更新用户资料
const updateProfile = async () => {
  if (!isEdit.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    // 只提交允许修改的字段
    const { realName, phone } = userForm.value
    await updateUserProfileService({ realName, phone })
    
    ElMessage.success('更新成功')
    isEdit.value = false
    getUserProfile() // 重新获取最新数据
  } catch (error) {
    console.error('更新失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取用户资料
const getUserProfile = async () => {
  try {
    const res = await getUserProfileService()
    const { userId, username, roleName, realName, phone } = res.data
    Object.assign(userForm.value, {
      userId,
      username,
      roleName,
      realName,
      phone
    })
  } catch (error) {
    console.error('获取用户资料失败:', error)
  }
}

// 页面加载时获取用户资料
onMounted(() => {
  getUserProfile()
})
</script>

<style lang="scss" scoped>
.user-info-container {
  max-width: 800px;
  margin: 20px auto;
  
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    span {
      font-size: 16px;
      font-weight: bold;
    }
  }
  
  .user-form {
    margin-top: 20px;
    padding: 20px;
    
    :deep(.el-form-item) {
      margin-bottom: 22px;
      
      .el-input {
        width: 350px;
        
        &.is-disabled {
          .el-input__wrapper {
            background-color: #f5f7fa;
          }
          
          .el-input__inner {
            color: #606266;
            -webkit-text-fill-color: #606266;
          }
        }
      }
    }
    
    .el-button {
      padding: 12px 20px;
      
      & + .el-button {
        margin-left: 12px;
      }
    }
  }
}
</style>