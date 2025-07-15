<template>
  <div class="temperature-container">
    <el-card class="box-card" :body-style="{ padding: '0px' }">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">体温记录管理</span>
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
        <!-- 体温记录表格 -->
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
          <el-table-column prop="temperature" label="体温(℃)" width="100" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="getTemperatureTagType(row.temperature)"
                effect="light"
              >
                {{ row.temperature }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="measureTime" label="测量时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatDateTime(row.measureTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="measurePosition" label="测量部位" width="120" align="center">
            <template #default="{ row }">
              {{ getMeasurePositionText(row.measurePosition) }}
            </template>
          </el-table-column>
          <el-table-column prop="notes" label="备注" min-width="180" />
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
            :current-page="currentPage"
            :page-size="pageSize"
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
      :title="dialogType === 'add' ? '添加体温记录' : '编辑体温记录'"
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
          
          <el-form-item label="体温(℃)" prop="temperature">
            <el-input-number
              v-model="formData.temperature"
              :min="35"
              :max="42"
              :precision="1"
              :step="0.1"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="测量部位" prop="measurePosition">
            <el-select
              v-model="formData.measurePosition"
              placeholder="请选择测量部位"
              style="width: 100%"
            >
              <el-option
                v-for="(pos, key) in MEASURE_POSITIONS"
                :key="key"
                :label="pos.label"
                :value="pos.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="测量时间" prop="measureTime">
            <el-date-picker
              v-model="formData.measureTime"
              type="datetime"
              placeholder="选择测量时间"
              style="width: 100%"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DDTHH:mm:ss"
            />
          </el-form-item>

          <el-form-item label="备注" prop="notes">
            <el-input
              v-model="formData.notes"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
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
import { ref, watch, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTemperatureRecordListService, getTemperatureRecordDetailService, addTemperatureRecordService, updateTemperatureRecordService, deleteTemperatureRecordService } from '@/api/temperature'
import { getMyChildrenService } from '@/api/child'
import { useTokenStore } from '@/stores/token'

const userStore = useTokenStore()
const roleId = userStore.roleId

// 列表数据
const loading = ref(false)
const recordList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedChildId = ref(null)
const childrenList = ref([])

// 表单数据
const dialogVisible = ref(false)
const dialogType = ref('add')
const submitting = ref(false)
const formRef = ref(null)

// 测量部位枚举（与后端完全匹配）
const MEASURE_POSITIONS = {
  MOUTH: {
    value: 'MOUTH',
    label: '口腔'
  },
  ARMPIT: {
    value: 'ARMPIT',
    label: '腋下'
  },
  EAR: {
    value: 'EAR',
    label: '耳温'
  }
}

// 表单数据
const formData = ref({
  childId: null,
  temperature: 36.5,
  measureTime: null,
  measurePosition: MEASURE_POSITIONS.MOUTH.value, // 使用枚举值
  notes: ''
})

// 表单验证规则
const formRules = {
  childId: [{ required: true, message: '请选择儿童', trigger: 'change' }],
  temperature: [{ required: true, message: '请输入体温', trigger: 'blur' }],
  measureTime: [{ required: true, message: '请选择测量时间', trigger: 'change' }],
  measurePosition: [{ required: true, message: '请选择测量部位', trigger: 'change' }]
}

// 获取体温标签类型
const getTemperatureTagType = (temperature) => {
  if (temperature >= 38) return 'danger'
  if (temperature >= 37.3) return 'warning'
  return 'success'
}

// 获取测量部位文本
const getMeasurePositionText = (position) => {
  return MEASURE_POSITIONS[position]?.label || position
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString()
}

// 获取儿童列表
const getChildrenList = async () => {
  try {
    const res = await getMyChildrenService()
    if (res.code === 0) {
      childrenList.value = res.data
      if (roleId === 3 && childrenList.value.length > 0) {
        selectedChildId.value = childrenList.value[0].childId
        getRecordList()
      }
    }
  } catch (error) {
    console.error('获取儿童列表失败:', error)
    ElMessage.error('获取儿童列表失败')
  }
}

// 获取体温记录列表
const getRecordList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      childId: selectedChildId.value
    }
    const res = await getTemperatureRecordListService(params)
    if (res.code === 0) {
      recordList.value = res.data.rows
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取体温记录失败:', error)
    ElMessage.error('获取体温记录失败')
  } finally {
    loading.value = false
  }
}

// 处理儿童选择变化
const handleChildChange = () => {
  currentPage.value = 1
  getRecordList()
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  getRecordList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  getRecordList()
}

// 添加记录
const handleAdd = () => {
  dialogType.value = 'add'
  formData.value = {
    childId: selectedChildId.value,
    temperature: 36.5,
    measureTime: new Date().toISOString().slice(0, 19),
    measurePosition: MEASURE_POSITIONS.MOUTH.value, // 使用枚举值
    notes: ''
  }
  dialogVisible.value = true
}

// 编辑记录
const handleEdit = async (row) => {
  dialogType.value = 'edit'
  try {
    const res = await getTemperatureRecordDetailService(row.recordId)
    if (res.code === 0) {
      formData.value = {
        recordId: res.data.recordId,
        temperature: Number(res.data.temperature),
        measureTime: res.data.measureTime ? res.data.measureTime.slice(0, 19) : null,
        measurePosition: res.data.measurePosition,
        notes: res.data.notes || ''
      }
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取记录详情失败:', error)
    ElMessage.error('获取记录详情失败')
  }
}

// 删除记录
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteTemperatureRecordService(row.recordId)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      getRecordList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const submitData = {
      temperature: Number(formData.value.temperature),
      measureTime: formData.value.measureTime,
      measurePosition: formData.value.measurePosition.toUpperCase(), // 确保是大写
      notes: formData.value.notes || ''
    }

    if (dialogType.value === 'add') {
      submitData.childId = selectedChildId.value
    }
    
    const service = dialogType.value === 'add' ? 
      addTemperatureRecordService : 
      (data) => updateTemperatureRecordService(formData.value.recordId, data)
    
    const res = await service(submitData)
    if (res.code === 0) {
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
      dialogVisible.value = false
      getRecordList()
    } else {
      ElMessage.error(res.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'))
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error !== 'cancel') {
      ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
    }
  } finally {
    submitting.value = false
  }
}

// 监听分页变化
watch([currentPage, pageSize], () => {
  getRecordList()
})

// 页面加载时获取数据
onMounted(() => {
  getChildrenList()
  if (roleId !== 3) {
    getRecordList()
  }
})
</script>

<style lang="scss" scoped>
.temperature-container {
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