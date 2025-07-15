<template>
  <el-card class="reset-password-container">
    <template #header>
      <div class="header">
        <span>重置密码</span>
      </div>
    </template>
    
    <el-form 
      :model="passwordForm" 
      :rules="rules"
      ref="formRef"
      label-width="120px"
      class="password-form"
    >
      <el-form-item label="原密码" prop="oldPassword">
        <el-input 
          v-model="passwordForm.oldPassword" 
          type="password"
          show-password
          placeholder="请输入原密码"
          clearable
        ></el-input>
      </el-form-item>
      
      <el-form-item label="新密码" prop="newPassword">
        <el-input 
          v-model="passwordForm.newPassword" 
          type="password"
          show-password
          placeholder="请输入新密码"
          clearable
        ></el-input>
      </el-form-item>
      
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input 
          v-model="passwordForm.confirmPassword" 
          type="password"
          show-password
          placeholder="请再次输入新密码"
          clearable
        ></el-input>
      </el-form-item>
      
      <el-form-item>
        <el-button 
          type="primary" 
          @click="handleResetPassword" 
          :loading="loading"
          :icon="Key"
        >
          确认修改
        </el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Key } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useTokenStore } from '@/stores/token'
import { updatePasswordService } from '@/api/user'

const router = useRouter()
const tokenStore = useTokenStore()
const formRef = ref(null)
const loading = ref(false)

// 表单数据
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (value.length < 6 || value.length > 16) {
      callback(new Error('密码长度应在6-16位之间'))
    } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
      callback(new Error('密码只能包含字母、数字和下划线'))
    } else {
      callback()
    }
  }
}

// 确认密码验证
const validateConfirmPass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 表单校验规则
const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPass, trigger: 'blur' }
  ]
}

// 重置密码
const handleResetPassword = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    // 发送包含所有密码字段的请求
    await updatePasswordService(passwordForm.value)
    
    ElMessage.success('密码修改成功，请重新登录')
    // 退出登录
    tokenStore.removeToken()
    router.push('/login')
  } catch (error) {
    if (error.response?.data?.msg) {
      ElMessage.error(error.response.data.msg)
    } else {
      ElMessage.error('修改密码失败')
    }
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
}
</script>

<style lang="scss" scoped>
.reset-password-container {
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
  
  .password-form {
    margin-top: 20px;
    padding: 20px;
    
    :deep(.el-form-item) {
      margin-bottom: 22px;
      
      .el-input {
        width: 350px;
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