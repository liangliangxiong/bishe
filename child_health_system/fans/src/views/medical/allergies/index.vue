<template>
  <div class="allergies-container">
    <el-card class="box-card" :body-style="{ padding: '0px' }">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">过敏记录管理</span>
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
            prop="child_name" 
            label="儿童姓名" 
            min-width="120"
          />
          <el-table-column prop="allergy_name" label="过敏源" min-width="150" />
          <el-table-column prop="severity" label="严重程度" width="120" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="getSeverityTagType(row.severity)"
                effect="light"
              >
                {{ getSeverityLabel(row.severity) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="diagnosis_date" label="诊断日期" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.diagnosis_date) }}
            </template>
          </el-table-column>
          <el-table-column prop="notes" label="备注" min-width="200" show-overflow-tooltip />
          <el-table-column prop="created_at" label="创建时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatDateTime(row.created_at) }}
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

        <div class="pagination-container">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @update:current-page="currentPage = $event"
            @update:page-size="pageSize = $event"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
          />
        </div>
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '添加过敏记录' : '编辑过敏记录'"
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
          
          <el-form-item label="过敏源" prop="allergyName">
            <el-input 
              v-model="formData.allergyName"
              placeholder="请输入过敏源名称"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="严重程度" prop="severity">
            <el-select
              v-model="formData.severity"
              placeholder="请选择严重程度"
              style="width: 100%"
            >
              <el-option
                v-for="item in severityOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="诊断日期" prop="diagnosisDate">
            <el-date-picker
              v-model="formData.diagnosisDate"
              type="date"
              placeholder="选择诊断日期"
              style="width: 100%"
              :disabled-date="disabledDate"
            />
          </el-form-item>
          
          <el-form-item label="备注" prop="notes">
            <el-input 
              v-model="formData.notes"
              type="textarea"
              placeholder="请输入备注信息"
              :rows="3"
              style="width: 100%"
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
import { formatDate, formatDateTime } from '@/utils/format'
import { getMyChildrenService } from '@/api/child'
import {
  getAllergyListService,
  addAllergyService,
  updateAllergyService,
  deleteAllergyService
} from '@/api/allergies'

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

// 严重程度选项
const severityOptions = [
  { label: '轻度', value: 'MILD' },
  { label: '中度', value: 'MODERATE' },
  { label: '重度', value: 'SEVERE' }
]

// 表单数据
const formData = ref({
  childId: null,
  allergyName: '',
  severity: '',
  diagnosisDate: null,
  notes: ''
})

// 表单校验规则
const formRules = {
  childId: [
    { required: true, message: '请选择儿童', trigger: 'change' }
  ],
  allergyName: [
    { required: true, message: '请输入过敏源名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在2-100个字符之间', trigger: 'blur' }
  ],
  severity: [
    { required: true, message: '请选择严重程度', trigger: 'change' }
  ],
  diagnosisDate: [
    { required: true, message: '请选择诊断日期', trigger: 'change' }
  ]
}

// 获取儿童列表
const fetchChildrenList = async () => {
  try {
    const res = await getMyChildrenService()
    if (res.code === 0) {
      childrenList.value = res.data || []
    }
  } catch (error) {
    console.error('获取儿童列表错误:', error)
  }
}

// 获取过敏记录列表
const fetchAllergyList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (selectedChildId.value) {
      params.childId = selectedChildId.value
    }
    
    const res = await getAllergyListService(params)
    console.log('过敏记录列表:', res)
    if (res.code === 0 && res.data) {
      recordList.value = res.data.rows || []
      total.value = res.data.total || 0
      console.log('处理后的列表数据:', recordList.value)
    } else {
      ElMessage.error(res.message || '获取过敏记录列表失败')
    }
  } catch (error) {
    console.error('获取过敏记录列表错误:', error)
    ElMessage.error('获取过敏记录列表失败')
  } finally {
    loading.value = false
  }
}

// 处理儿童选择变化
const handleChildChange = () => {
  currentPage.value = 1
  fetchAllergyList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchAllergyList()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchAllergyList()
}

// 禁用未来日期
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 获取严重程度标签类型
const getSeverityTagType = (severity) => {
  switch (severity) {
    case 'MILD':
      return 'success'
    case 'MODERATE':
      return 'warning'
    case 'SEVERE':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取严重程度显示文本
const getSeverityLabel = (severity) => {
  const option = severityOptions.find(opt => opt.value === severity)
  return option ? option.label : severity
}

// 添加记录
const handleAdd = () => {
  dialogType.value = 'add'
  formData.value = {
    childId: selectedChildId.value,
    allergyName: '',
    severity: '',
    diagnosisDate: null,
    notes: ''
  }
  dialogVisible.value = true
}

// 编辑记录
const handleEdit = (row) => {
  console.log('编辑的行数据:', row)
  dialogType.value = 'edit'
  formData.value = {
    recordId: row.allergy_id,
    childId: row.child_id,
    allergyName: row.allergy_name,
    severity: row.severity ? row.severity.toUpperCase() : '',
    diagnosisDate: row.diagnosis_date ? new Date(row.diagnosis_date) : null,
    notes: row.notes || ''
  }
  dialogVisible.value = true
}

// 删除记录
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该过敏记录吗？', '提示', {
      type: 'warning'
    })
    
    const res = await deleteAllergyService(row.allergy_id)
    console.log('删除响应:', res)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      // 如果当前页只有一条数据，且不是第一页，则跳转到上一页
      if (recordList.value.length === 1 && currentPage.value > 1) {
        currentPage.value--
      }
      await fetchAllergyList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除过敏记录错误:', error)
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
      childId: formData.value.childId,
      allergyName: formData.value.allergyName,
      severity: formData.value.severity.toUpperCase(),
      diagnosisDate: formatDate(formData.value.diagnosisDate),
      notes: formData.value.notes || ''
    }

    if (dialogType.value === 'edit') {
      submitData.allergyId = formData.value.recordId
    }
    
    console.log('提交的数据:', submitData)
    let res
    if (dialogType.value === 'add') {
      res = await addAllergyService(submitData)
    } else {
      res = await updateAllergyService(submitData.allergyId, submitData)
    }

    console.log('提交响应:', res)
    if (res.code === 0) {
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
      dialogVisible.value = false
      // 重新获取列表
      await fetchAllergyList()
    } else {
      ElMessage.error(res.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'))
    }
  } catch (error) {
    console.error('表单提交错误:', error)
    if (error !== 'cancel') {
      ElMessage.error('保存过敏记录失败')
    }
  } finally {
    submitting.value = false
  }
}

// 在组件挂载时获取数据
onMounted(async () => {
  if (roleId.value !== 3) {
    await fetchChildrenList()
  }
  await fetchAllergyList()
})
</script>

<style lang="scss" scoped>
.allergies-container {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .header-title {
    font-size: 16px;
    font-weight: bold;
  }

  .header-operations {
    display: flex;
    align-items: center;
    gap: 10px;

    .search-input {
      width: 200px;
    }
  }

  .table-container {
    padding: 20px;
  }

  .dialog-form {
    padding: 20px 0;
  }

  .dialog-footer {
    text-align: right;
    padding-top: 20px;
  }

  .pagination-container {
    padding: 20px;
    text-align: right;
  }
}
</style> 