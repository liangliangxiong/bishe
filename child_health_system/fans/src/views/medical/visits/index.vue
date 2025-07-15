<template>
  <div class="visits-container">
    <el-card class="box-card" :body-style="{ padding: '0px' }">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">就医记录管理</span>
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
        <!-- 就医记录表格 -->
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
          <el-table-column prop="hospital_name" label="医院名称" min-width="180" />
          <el-table-column prop="department" label="就诊科室" min-width="120" />
          <el-table-column prop="diagnosis" label="诊断结果" min-width="180" show-overflow-tooltip />
          <el-table-column prop="treatment" label="治疗方案" min-width="180" show-overflow-tooltip />
          <el-table-column prop="visit_date" label="就诊日期" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.visit_date) }}
            </template>
          </el-table-column>
          <el-table-column prop="doctor_name" label="就诊医生" width="120" align="center" />
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

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            @update:current-page="currentPage = $event"
            @update:page-size="pageSize = $event"
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
      :title="dialogType === 'add' ? '添加就医记录' : '编辑就医记录'"
      v-model="dialogVisible"
      width="600px"
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
          
          <el-form-item label="医院名称" prop="hospitalName">
            <el-input v-model="formData.hospitalName" placeholder="请输入医院名称" />
          </el-form-item>
          
          <el-form-item label="就诊科室" prop="department">
            <el-input v-model="formData.department" placeholder="请输入就诊科室" />
          </el-form-item>
          
          <el-form-item label="就诊日期" prop="visitDate">
            <el-date-picker
              v-model="formData.visitDate"
              type="date"
              placeholder="选择日期"
              style="width: 100%"
              :disabled-date="disabledDate"
            />
          </el-form-item>

          <el-form-item label="诊断结果" prop="diagnosis">
            <el-input
              v-model="formData.diagnosis"
              type="textarea"
              rows="3"
              placeholder="请输入诊断结果"
            />
          </el-form-item>

          <el-form-item label="治疗方案" prop="treatment">
            <el-input
              v-model="formData.treatment"
              type="textarea"
              rows="3"
              placeholder="请输入治疗方案"
            />
          </el-form-item>

          <el-form-item label="备注说明" prop="notes">
            <el-input
              v-model="formData.notes"
              type="textarea"
              rows="2"
              placeholder="请输入备注说明（选填）"
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
  getMedicalRecordListService,
  addMedicalRecordService,
  updateMedicalRecordService,
  deleteMedicalRecordService
} from '@/api/medicalRecords'
import { formatDate, formatDateTime } from '@/utils/format'

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
  hospitalName: '',
  department: '',
  visitDate: null,
  diagnosis: '',
  treatment: '',
  notes: ''
})

// 表单校验规则
const formRules = {
  childId: [
    { required: true, message: '请选择儿童', trigger: 'change' }
  ],
  hospitalName: [
    { required: true, message: '请输入医院名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在2-100个字符之间', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入就诊科室', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符之间', trigger: 'blur' }
  ],
  visitDate: [
    { required: true, message: '请选择就诊日期', trigger: 'change' }
  ],
  diagnosis: [
    { required: true, message: '请输入诊断结果', trigger: 'blur' },
    { min: 2, max: 500, message: '长度在2-500个字符之间', trigger: 'blur' }
  ],
  treatment: [
    { required: true, message: '请输入治疗方案', trigger: 'blur' },
    { min: 2, max: 500, message: '长度在2-500个字符之间', trigger: 'blur' }
  ]
}

// 禁用未来日期
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 获取儿童列表
const getChildrenList = async () => {
  try {
    const res = await getMyChildrenService()
    childrenList.value = res.data
  } catch (error) {
    console.error('获取儿童列表失败:', error)
  }
}

// 获取就医记录列表
const getRecordList = async () => {
  loading.value = true
  try {
    const res = await getMedicalRecordListService({
      page: currentPage.value,
      pageSize: pageSize.value,
      childId: selectedChildId.value
    })
    recordList.value = res.data.rows
    total.value = res.data.total
  } catch (error) {
    console.error('获取就医记录列表失败:', error)
    ElMessage.error('获取就医记录列表失败')
  } finally {
    loading.value = false
  }
}

// 处理儿童选择变化
const handleChildChange = () => {
  currentPage.value = 1
  getRecordList()
}

// 处理分页变化
const handleSizeChange = (val) => {
  pageSize.value = val
  getRecordList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getRecordList()
}

// 添加记录
const handleAdd = () => {
  dialogType.value = 'add'
  formData.value = {
    childId: selectedChildId.value,
    hospitalName: '',
    department: '',
    visitDate: null,
    diagnosis: '',
    treatment: '',
    notes: ''
  }
  dialogVisible.value = true
}

// 编辑记录
const handleEdit = (row) => {
  dialogType.value = 'edit'
  formData.value = {
    recordId: row.record_id,
    childId: row.child_id,
    hospitalName: row.hospital_name,
    department: row.department,
    visitDate: row.visit_date,
    diagnosis: row.diagnosis,
    treatment: row.treatment,
    notes: row.notes
  }
  dialogVisible.value = true
}

// 删除记录
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条就医记录吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await deleteMedicalRecordService(row.record_id)
    ElMessage.success('删除成功')
    if (recordList.value.length === 1 && currentPage.value > 1) {
      currentPage.value--
    }
    getRecordList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除就医记录失败:', error)
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
    
    if (dialogType.value === 'add') {
      await addMedicalRecordService(formData.value)
      ElMessage.success('添加成功')
    } else {
      await updateMedicalRecordService(formData.value.recordId, formData.value)
      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    getRecordList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交就医记录失败:', error)
      ElMessage.error('提交失败')
    }
  } finally {
    submitting.value = false
  }
}

// 页面加载时获取数据
onMounted(async () => {
  if (roleId.value !== 3) {
    await getChildrenList()
  }
  getRecordList()
})
</script>

<style lang="scss" scoped>
.visits-container {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;

      .header-title {
        font-size: 16px;
        font-weight: bold;
      }

      .header-count {
        margin-left: 8px;
      }
    }

    .header-operations {
      display: flex;
      align-items: center;
      gap: 12px;

      .search-input {
        width: 200px;
      }
    }
  }

  .table-container {
    padding: 20px;

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}

.dialog-form {
  padding: 20px 20px 0;
}

.dialog-footer {
  text-align: right;
  padding: 0 20px 20px;
}
</style> 