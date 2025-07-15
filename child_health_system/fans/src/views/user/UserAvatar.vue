<template>
  <el-card class="avatar-container">
    <template #header>
      <div class="card-header">
        <span>更换头像</span>
      </div>
    </template>
    
    <div class="avatar-content">
      <div class="avatar-preview">
        <img :src="currentAvatar" alt="当前头像" class="avatar-image" />
      </div>
      
      <div class="upload-area">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :http-request="handleAvatarUpload"
        >
          <el-button type="primary" size="large">选择图片</el-button>
          <template #tip>
            <div class="el-upload__tip">
              只能上传 jpg/png 文件，且不超过 2MB
            </div>
          </template>
        </el-upload>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useTokenStore } from '@/stores/token.js'
// import { userAvatarUpdateService } from '@/api/user.js'

const tokenStore = useTokenStore()
const currentAvatar = ref(tokenStore.avatar || '/src/assets/default.png')

// 上传前校验
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 处理头像上传
const handleAvatarUpload = async (options) => {
  try {
    const file = options.file
    // 这里可以调用后端API上传图片
    // const formData = new FormData()
    // formData.append('avatar', file)
    // const res = await userAvatarUpdateService(formData)
    
    // 模拟上传成功
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = (e) => {
      currentAvatar.value = e.target.result
      ElMessage.success('头像更新成功')
    }
  } catch (error) {
    ElMessage.error('上传失败')
  }
}
</script>

<style lang="scss" scoped>
.avatar-container {
  width: 100%;
  max-width: 800px;
  margin: 20px auto;

  .card-header {
    font-weight: bold;
  }

  .avatar-content {
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding: 20px;

    .avatar-preview {
      text-align: center;
      
      .avatar-image {
        width: 200px;
        height: 200px;
        border-radius: 50%;
        object-fit: cover;
        border: 1px solid #dcdfe6;
      }
    }

    .upload-area {
      text-align: center;
      
      .el-upload__tip {
        color: #909399;
        font-size: 14px;
        margin-top: 10px;
      }
    }
  }
}
</style>