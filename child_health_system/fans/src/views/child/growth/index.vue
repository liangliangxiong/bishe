<template>
  <div class="growth-container">
    <el-card class="box-card" :body-style="{ padding: '0px' }">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">生长记录管理</span>
            <el-tag type="info" effect="dark" class="header-count">
              共 {{ total }} 条记录
            </el-tag>
          </div>
          <div class="header-operations">
            <!-- 儿童选择（医生和管理员可见） -->
            <el-select
              v-if="roleId !== 3"
              v-model="selectedChildId"
              placeholder="选择儿童"
              clearable
              class="search-input"
              @change="handleChildChange"
            >
              <el-option
                v-for="child in childrenList"
                :key="child.childId"
                :label="child.name"
                :value="child.childId"
              />
            </el-select>
            
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>添加记录
            </el-button>
          </div>
        </div>
      </template>

      <div class="table-container">
        <!-- 生长记录表格 -->
        <el-table
          v-loading="loading"
          :data="recordList"
          border
          stripe
          style="width: 100%"
          :header-cell-style="{
            background: '#f5f7fa',
            color: '#606266',
            fontWeight: 'bold'
          }"
        >
          <el-table-column 
            v-if="roleId !== 3"
            prop="childName" 
            label="儿童姓名" 
            min-width="120"
          />
          <el-table-column prop="height" label="身高(cm)" width="100" align="center" />
          <el-table-column prop="weight" label="体重(kg)" width="100" align="center" />
          <el-table-column prop="bmi" label="BMI" width="80" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="getBmiTagType(row.bmi)"
                effect="light"
              >
                {{ row.bmi }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="measureDate" label="测量日期" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.measureDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatDateTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <el-button-group>
                <el-button 
                  type="primary" 
                  @click="handleEdit(row)" 
                  :icon="Edit"
                  size="small"
                >
                  编辑
                </el-button>
                <el-button 
                  type="danger" 
                  @click="handleDelete(row)" 
                  :icon="Delete"
                  size="small"
                >
                  删除
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:currentPage="currentPage"
            v-model:pageSize="pageSize"
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
      :title="dialogType === 'add' ? '添加生长记录' : '编辑生长记录'"
      v-model="dialogVisible"
      width="500px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <div class="dialog-form">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
        >
          <el-form-item 
            label="选择儿童" 
            prop="childId"
            v-if="roleId !== 3 && dialogType === 'add'"
          >
            <el-select
              v-model="formData.childId"
              placeholder="请选择儿童"
              style="width: 100%"
            >
              <el-option
                v-for="child in childrenList"
                :key="child.childId"
                :label="child.name"
                :value="child.childId"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="身高(cm)" prop="height">
            <el-input-number
              v-model="formData.height"
              :min="0"
              :max="250"
              :precision="1"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="体重(kg)" prop="weight">
            <el-input-number
              v-model="formData.weight"
              :min="0"
              :max="200"
              :precision="1"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="测量日期" prop="measureDate">
            <el-date-picker
              v-model="formData.measureDate"
              type="date"
              placeholder="选择日期"
              style="width: 100%"
              :disabled-date="disabledDate"
            />
          </el-form-item>
        </el-form>
      </div>
      
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
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTokenStore } from '@/stores/token'
import { getMyChildrenService } from '@/api/child'
import {
  getGrowthRecordListService,
  getChildGrowthRecordsService,
  addGrowthRecordService,
  updateGrowthRecordService,
  deleteGrowthRecordService
} from '@/api/growth'

const tokenStore = useTokenStore()
const roleId = ref(tokenStore.roleId)

// 表格数据
const loading = ref(false)
const recordList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 儿童选择
const childrenList = ref([])
const selectedChildId = ref(null)

// 对话框控制
const dialogVisible = ref(false)
const dialogType = ref('add') // add or edit
const submitting = ref(false)
const formRef = ref(null)

// 表单数据
const formData = ref({
  childId: null,
  height: null,
  weight: null,
  measureDate: null
})

// 表单校验规则
const formRules = {
  childId: [
    { required: true, message: '请选择儿童', trigger: 'change' }
  ],
  height: [
    { required: true, message: '请输入身高', trigger: 'blur' },
    { type: 'number', min: 30, max: 250, message: '身高范围在30-250cm之间', trigger: 'blur' }
  ],
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' },
    { type: 'number', min: 2, max: 200, message: '体重范围在2-200kg之间', trigger: 'blur' }
  ],
  measureDate: [
    { required: true, message: '请选择测量日期', trigger: 'change' }
  ]
}

// 获取儿童列表
const getChildrenList = async () => {
  try {
    const res = await getMyChildrenService()
    if (res.code === 0) {
      childrenList.value = res.data
      // 如果是家长，自动选择第一个儿童
      if (roleId === 3 && childrenList.value.length > 0) {
        selectedChildId.value = childrenList.value[0].childId
        getRecordList()
      }
    }
  } catch (error) {
    console.error('获取儿童列表失败:', error)
  }
}

// 获取生长记录列表
const getRecordList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      childId: selectedChildId.value
    }
    const res = await getGrowthRecordListService(params)
    if (res.code === 0) {
      recordList.value = res.data.rows
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取生长记录失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 处理儿童选择变化
const handleChildChange = () => {
  currentPage.value = 1
  getRecordList()
}

// 处理添加按钮点击
const handleAdd = () => {
  dialogType.value = 'add'
  formData.value = {
    childId: selectedChildId.value,
    height: null,
    weight: null,
    measureDate: new Date()
  }
  dialogVisible.value = true
}

// 处理编辑按钮点击
const handleEdit = (row) => {
  dialogType.value = 'edit'
  formData.value = {
    recordId: row.recordId,
    childId: row.childId,
    height: row.height,
    weight: row.weight,
    measureDate: row.measureDate
  }
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      type: 'warning'
    })
    await deleteGrowthRecordService(row.recordId)
    ElMessage.success('删除成功')
    getRecordList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (dialogType.value === 'add') {
          await addGrowthRecordService(formData.value)
          ElMessage.success('添加成功')
        } else {
          await updateGrowthRecordService(formData.value.recordId, formData.value)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        getRecordList()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  getRecordList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getRecordList()
}

// 工具方法
const formatDate = (date) => {
  if (!date) return '-'
  return date.split('T')[0]
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  const date = new Date(datetime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

const getBmiTagType = (bmi) => {
  if (!bmi) return ''
  if (bmi < 18.5) return 'warning'
  if (bmi > 24.9) return 'danger'
  return 'success'
}

const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 页面加载时获取数据
onMounted(() => {
  getChildrenList()
  if (roleId !== 3) {
    getRecordList()
  }
})
</script>

<style lang="scss" scoped>
.growth-container {
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

        .header-count {
          margin-left: 8px;
        }
      }

      .header-operations {
        display: flex;
        gap: 16px;

        .search-input {
          width: 200px;
        }
      }
    }
  }

  .table-container {
    padding: 20px;

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
  }

  .pagination-container {
    margin-top: 20px;
    padding: 10px 20px;
    display: flex;
    justify-content: flex-end;
    background-color: #fff;
    border-top: 1px solid #ebeef5;
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
    padding: 10px 20px 20px;
    text-align: right;
  }
}
</style> 