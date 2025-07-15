<template>
  <div class="vaccinations-container">
    <el-card class="box-card" :body-style="{ padding: '0px' }">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">疫苗接种管理</span>
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
          <el-table-column prop="vaccine_name" label="疫苗名称" min-width="150" />
          <el-table-column prop="vaccination_date" label="接种日期" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.vaccination_date) }}
            </template>
          </el-table-column>
          <el-table-column prop="next_due_date" label="下次接种日期" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.next_due_date) }}
            </template>
          </el-table-column>
          <el-table-column prop="doctor_name" label="医生" width="120" align="center" />
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
      :title="dialogType === 'add' ? '添加接种记录' : '编辑接种记录'"
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
          
          <el-form-item label="疫苗名称" prop="vaccineName">
            <el-input 
              v-model="formData.vaccineName"
              placeholder="请输入疫苗名称"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="接种日期" prop="vaccinationDate">
            <el-date-picker
              v-model="formData.vaccinationDate"
              type="date"
              placeholder="选择接种日期"
              style="width: 100%"
              :disabled-date="disabledDate"
            />
          </el-form-item>
          
          <el-form-item label="下次接种" prop="nextDueDate">
            <el-date-picker
              v-model="formData.nextDueDate"
              type="date"
              placeholder="选择下次接种日期"
              style="width: 100%"
              :disabled-date="disabledStartDate"
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
  getVaccinationListService,
  addVaccinationService,
  updateVaccinationService,
  deleteVaccinationService
} from '@/api/vaccinations'

const tokenStore = useTokenStore()
const roleId = ref(tokenStore.roleId)
const doctorId = ref(tokenStore.roleId === 2 ? tokenStore.userId : null)

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
  vaccineName: '',
  vaccinationDate: null,
  nextDueDate: null,
  notes: ''
})

// 表单校验规则
const formRules = {
  childId: [
    { required: true, message: '请选择儿童', trigger: 'change' }
  ],
  vaccineName: [
    { required: true, message: '请输入疫苗名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在2-100个字符之间', trigger: 'blur' }
  ],
  vaccinationDate: [
    { required: true, message: '请选择接种日期', trigger: 'change' }
  ],
  nextDueDate: [
    { required: true, message: '请选择下次接种日期', trigger: 'change' }
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

// 获取接种记录列表
const fetchVaccinationList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (selectedChildId.value) {
      params.childId = selectedChildId.value
    }
    
    const res = await getVaccinationListService(params)
    console.log('接种记录列表:', res)
    if (res.code === 0 && res.data) {
      recordList.value = res.data.rows || []
      total.value = res.data.total || 0
      console.log('处理后的列表数据:', recordList.value)
    } else {
      ElMessage.error(res.message || '获取接种记录列表失败')
    }
  } catch (error) {
    console.error('获取接种记录列表错误:', error)
    ElMessage.error('获取接种记录列表失败')
  } finally {
    loading.value = false
  }
}

// 处理儿童选择变化
const handleChildChange = () => {
  currentPage.value = 1
  fetchVaccinationList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchVaccinationList()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchVaccinationList()
}

// 禁用未来日期
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 禁用早于接种日期的下次接种日期
const disabledStartDate = (time) => {
  if (!formData.value.vaccinationDate) {
    return false
  }
  return time.getTime() < formData.value.vaccinationDate.getTime()
}

// 添加记录
const handleAdd = () => {
  if (roleId.value !== 2) {
    ElMessage.warning('只有医生可以添加接种记录')
    return
  }
  
  dialogType.value = 'add'
  formData.value = {
    childId: selectedChildId.value,
    vaccineName: '',
    vaccinationDate: null,
    nextDueDate: null,
    notes: ''
  }
  dialogVisible.value = true
}

// 编辑记录
const handleEdit = (row) => {
  if (roleId.value !== 2) {
    ElMessage.warning('只有医生可以编辑接种记录')
    return
  }
  
  console.log('编辑的行数据:', row)
  dialogType.value = 'edit'
  formData.value = {
    recordId: row.vaccination_id,
    childId: row.child_id,
    vaccineName: row.vaccine_name,
    vaccinationDate: row.vaccination_date ? new Date(row.vaccination_date) : null,
    nextDueDate: row.next_due_date ? new Date(row.next_due_date) : null,
    notes: row.notes || ''
  }
  dialogVisible.value = true
}

// 删除记录
const handleDelete = async (row) => {
  if (roleId.value !== 2) {
    ElMessage.warning('只有医生可以删除接种记录')
    return
  }
  
  try {
    await ElMessageBox.confirm('确认删除该接种记录吗？', '提示', {
      type: 'warning'
    })
    
    const res = await deleteVaccinationService(row.vaccination_id)
    console.log('删除响应:', res)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      // 如果当前页只有一条数据，且不是第一页，则跳转到上一页
      if (recordList.value.length === 1 && currentPage.value > 1) {
        currentPage.value--
      }
      await fetchVaccinationList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除接种记录错误:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 如果不是医生，不能提交
    if (roleId.value !== 2) {
      ElMessage.error('只有医生可以添加或修改接种记录')
      return
    }

    // 确保有医生ID
    if (!doctorId.value) {
      ElMessage.error('医生ID不能为空')
      return
    }
    
    submitting.value = true
    const submitData = {
      child_id: formData.value.childId,
      vaccine_name: formData.value.vaccineName,
      vaccination_date: formatDate(formData.value.vaccinationDate),
      next_due_date: formatDate(formData.value.nextDueDate),
      notes: formData.value.notes || '',
      doctor_id: doctorId.value
    }

    if (dialogType.value === 'edit') {
      submitData.vaccination_id = formData.value.recordId
    }
    
    console.log('提交的数据:', submitData)
    let res
    if (dialogType.value === 'add') {
      res = await addVaccinationService(submitData)
    } else {
      res = await updateVaccinationService(submitData.vaccination_id, submitData)
    }

    console.log('提交响应:', res)
    if (res.code === 0) {
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
      dialogVisible.value = false
      // 重新获取列表
      await fetchVaccinationList()
    } else {
      ElMessage.error(res.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'))
    }
  } catch (error) {
    console.error('表单提交错误:', error)
    if (error !== 'cancel') {
      ElMessage.error('保存接种记录失败')
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
  await fetchVaccinationList()
})
</script>

<style lang="scss" scoped>
.vaccinations-container {
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