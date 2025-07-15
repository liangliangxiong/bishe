<template>
  <div class="medications-container">
    <el-card class="box-card" :body-style="{ padding: '0px' }">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">用药记录管理</span>
            <el-tag type="info" effect="dark" class="header-count">
              共 {{ total }} 条记录
            </el-tag>
          </div>
          <div class="header-operations">
            <!-- 就医记录选择 -->
            <el-select
              v-model="selectedMedicalRecordId"
              placeholder="选择就医记录"
              clearable
              class="search-input"
              @change="handleMedicalRecordChange"
            >
              <el-option
                v-for="record in medicalRecordList"
                :key="record.record_id"
                :label="formatMedicalRecordLabel(record)"
                :value="record.record_id"
              />
            </el-select>
            
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>添加记录
            </el-button>
          </div>
        </div>
      </template>

      <div class="table-container">
        <!-- 用药记录表格 -->
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
          <el-table-column prop="medicine_name" label="药品名称" min-width="150" />
          <el-table-column prop="dosage" label="剂量" min-width="100" />
          <el-table-column prop="frequency" label="服用频率" min-width="120" />
          <el-table-column prop="start_date" label="开始日期" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.start_date) }}
            </template>
          </el-table-column>
          <el-table-column prop="end_date" label="结束日期" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.end_date) }}
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
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '添加用药记录' : '编辑用药记录'"
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
            label="就医记录" 
            prop="medical_record_id"
            v-if="dialogType === 'add'"
          >
            <el-select
              v-model="formData.medical_record_id"
              placeholder="请选择就医记录"
              style="width: 100%"
            >
              <el-option
                v-for="record in medicalRecordList"
                :key="record.record_id"
                :label="formatMedicalRecordLabel(record)"
                :value="record.record_id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="药品名称" prop="medicine_name">
            <el-input 
              v-model="formData.medicine_name"
              placeholder="请输入药品名称"
            />
          </el-form-item>
          
          <el-form-item label="剂量" prop="dosage">
            <el-input 
              v-model="formData.dosage"
              placeholder="请输入用药剂量"
            />
          </el-form-item>
          
          <el-form-item label="服用频率" prop="frequency">
            <el-input 
              v-model="formData.frequency"
              placeholder="请输入服用频率"
            />
          </el-form-item>
          
          <el-form-item label="开始日期" prop="start_date">
            <el-date-picker
              v-model="formData.start_date"
              type="date"
              placeholder="选择开始日期"
              style="width: 100%"
              :disabled-date="disabledDate"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item label="结束日期" prop="end_date">
            <el-date-picker
              v-model="formData.end_date"
              type="date"
              placeholder="选择结束日期"
              style="width: 100%"
              :disabled-date="disabledStartDate"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item label="备注" prop="notes">
            <el-input 
              v-model="formData.notes"
              type="textarea"
              placeholder="请输入备注信息"
              :rows="3"
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
import { getMedicalRecordListService } from '@/api/medicalRecords'
import {
  getMedicationListService,
  addMedicationRecordService,
  updateMedicationRecordService,
  deleteMedicationRecordService
} from '@/api/medicationRecords'

const tokenStore = useTokenStore()
const roleId = ref(tokenStore.roleId)

// 表格数据
const loading = ref(false)
const recordList = ref([])
const total = ref(0)

// 就医记录选择
const medicalRecordList = ref([])
const selectedMedicalRecordId = ref(null)

// 对话框控制
const dialogVisible = ref(false)
const dialogType = ref('add') // add or edit
const submitting = ref(false)
const formRef = ref(null)

// 表单数据
const formData = ref({
  medical_record_id: null,
  medicine_name: '',
  dosage: '',
  frequency: '',
  start_date: '',
  end_date: '',
  notes: ''
})

// 表单校验规则
const formRules = {
  medical_record_id: [
    { required: true, message: '请选择就医记录', trigger: 'change' }
  ],
  medicine_name: [
    { required: true, message: '请输入药品名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在2-100个字符之间', trigger: 'blur' }
  ],
  dosage: [
    { required: true, message: '请输入用药剂量', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在1-50个字符之间', trigger: 'blur' }
  ],
  frequency: [
    { required: true, message: '请输入服用频率', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符之间', trigger: 'blur' }
  ],
  start_date: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  end_date: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ]
}

// 获取就医记录列表
const fetchMedicalRecordList = async () => {
  try {
    const res = await getMedicalRecordListService({
      page: 1,
      pageSize: 1000
    })
    console.log('就医记录列表响应:', res)
    if (res.code === 0 && res.data && res.data.rows) {
      medicalRecordList.value = res.data.rows
      if (selectedMedicalRecordId.value) {
        fetchMedicationList()
      }
    } else {
      medicalRecordList.value = []
      ElMessage.error(res.message || '获取就医记录列表失败')
    }
  } catch (error) {
    console.error('获取就医记录列表失败:', error)
    ElMessage.error('获取就医记录列表失败')
    medicalRecordList.value = []
  }
}

// 获取用药记录列表
const fetchMedicationList = async () => {
  loading.value = true
  try {
    console.log('请求用药记录，就医记录ID:', selectedMedicalRecordId.value)
    const res = await getMedicationListService(selectedMedicalRecordId.value || 0)
    console.log('用药记录列表响应:', res)
    if (res.code === 0) {
      recordList.value = res.data || []
      total.value = recordList.value.length
      console.log('处理后的用药记录:', recordList.value)
    } else {
      recordList.value = []
      total.value = 0
      ElMessage.error(res.message || '获取用药记录列表失败')
    }
  } catch (error) {
    console.error('获取用药记录列表失败:', error)
    ElMessage.error('获取用药记录失败')
    recordList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 格式化就医记录标签
const formatMedicalRecordLabel = (record) => {
  if (!record) return ''
  const visitDate = record.visit_date || ''
  const hospitalName = record.hospital_name || ''
  const diagnosis = record.diagnosis || ''
  return `${formatDate(visitDate)} - ${hospitalName} - ${diagnosis}`
}

// 禁用未来日期
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 禁用早于开始日期的结束日期
const disabledStartDate = (time) => {
  if (!formData.value.start_date) {
    return time.getTime() > Date.now()
  }
  const startTime = new Date(formData.value.start_date).getTime()
  return time.getTime() < startTime || time.getTime() > Date.now()
}

// 就医记录变更处理
const handleMedicalRecordChange = () => {
  console.log('选择的就医记录ID:', selectedMedicalRecordId.value)
  fetchMedicationList()
}

// 添加记录
const handleAdd = () => {
  dialogType.value = 'add'
  formData.value = {
    medical_record_id: selectedMedicalRecordId.value || null,
    medicine_name: '',
    dosage: '',
    frequency: '',
    start_date: '',
    end_date: '',
    notes: ''
  }
  dialogVisible.value = true
}

// 编辑记录
const handleEdit = (row) => {
  console.log('编辑的行数据:', row)
  dialogType.value = 'edit'
  formData.value = {
    record_id: row.record_id,
    medical_record_id: row.medical_record_id,
    medicine_name: row.medicine_name,
    dosage: row.dosage,
    frequency: row.frequency,
    start_date: row.start_date,
    end_date: row.end_date,
    notes: row.notes || ''
  }
  dialogVisible.value = true
}

// 删除记录
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条用药记录吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    const res = await deleteMedicationRecordService(row.record_id)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      fetchMedicationList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用药记录失败:', error)
      ElMessage.error('删除用药记录失败')
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
      medical_record_id: formData.value.medical_record_id,
      medicine_name: formData.value.medicine_name,
      dosage: formData.value.dosage,
      frequency: formData.value.frequency,
      start_date: formData.value.start_date,
      end_date: formData.value.end_date,
      notes: formData.value.notes || ''
    }

    if (dialogType.value === 'edit') {
      submitData.record_id = formData.value.record_id
    }
    
    console.log('提交的数据:', submitData)
    let res
    if (dialogType.value === 'add') {
      res = await addMedicationRecordService(submitData)
    } else {
      res = await updateMedicationRecordService(submitData.record_id, submitData)
    }

    if (res.code === 0) {
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
      dialogVisible.value = false
      fetchMedicationList()
    } else {
      ElMessage.error(res.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'))
    }
  } catch (error) {
    console.error('保存用药记录失败:', error)
    if (error !== 'cancel') {
      ElMessage.error('保存用药记录失败')
    }
  } finally {
    submitting.value = false
  }
}

// 在组件挂载时获取数据
onMounted(async () => {
  await Promise.all([
    fetchMedicalRecordList(),
    fetchMedicationList()
  ])
})
</script>

<style lang="scss" scoped>
.medications-container {
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
      width: 300px;
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
}
</style> 