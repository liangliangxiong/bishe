<template>
  <div class="diet-container">
    <el-card class="box-card" :body-style="{ padding: '0px' }">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">饮食记录管理</span>
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
                :key="child.child_id"
                :label="child.real_name"
                :value="child.child_id"
              />
            </el-select>
            
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>添加记录
            </el-button>
          </div>
        </div>
      </template>

      <div class="table-container">
        <!-- 表格区域 -->
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
          <el-table-column prop="child_name" label="儿童姓名" min-width="100" />
          <el-table-column prop="meal_type" label="餐次" min-width="70">
            <template #default="{ row }">
              <el-tag :type="getMealTypeTag(row.meal_type)">
                {{ getMealTypeLabel(row.meal_type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="meal_time" label="用餐时间" min-width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.meal_time) }}
            </template>
          </el-table-column>
          <el-table-column prop="food_items" label="食物项目" min-width="200" />
          <el-table-column prop="amount" label="食用量" min-width="150" />
          <el-table-column prop="notes" label="备注" min-width="150" />
          <el-table-column label="操作" width="160" fixed="right">
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
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="dialog-form"
      >
        <el-form-item
          v-if="roleId !== 3"
          label="儿童"
          prop="childId"
        >
          <el-select
            v-model="formData.childId"
            placeholder="请选择儿童"
          >
            <el-option
              v-for="child in childrenList"
              :key="child.child_id"
              :label="child.real_name"
              :value="child.child_id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="餐次" prop="mealType">
          <el-select v-model="formData.mealType" placeholder="请选择餐次">
            <el-option label="早餐" value="breakfast" />
            <el-option label="午餐" value="lunch" />
            <el-option label="晚餐" value="dinner" />
            <el-option label="点心" value="snack" />
          </el-select>
        </el-form-item>

        <el-form-item label="用餐时间" prop="mealTime">
          <el-date-picker
            v-model="formData.mealTime"
            type="datetime"
            placeholder="选择用餐时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="食物项目" prop="foodItems">
          <el-input
            v-model="formData.foodItems"
            placeholder="请输入食物项目，多个用逗号分隔"
          />
        </el-form-item>

        <el-form-item label="食用量" prop="amount">
          <el-input
            v-model="formData.amount"
            placeholder="请输入食用量"
          />
        </el-form-item>

        <el-form-item label="备注" prop="notes">
          <el-input
            v-model="formData.notes"
            type="textarea"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTokenStore } from '@/stores/token'
import { getMyChildrenService, getChildListService } from '@/api/child'
import { 
  getDietRecordListService,
  addDietRecordService,
  updateDietRecordService,
  deleteDietRecordService
} from '@/api/dietRecords'
import { formatDateTime } from '@/utils/format'

// 从 store 获取角色信息
const { roleId } = useTokenStore()

// 数据列表相关
const loading = ref(false)
const recordList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 儿童列表相关
const childrenList = ref([])
const selectedChildId = ref(null)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formData = ref({
  childId: null,
  mealType: '',
  mealTime: '',
  foodItems: '',
  amount: '',
  notes: ''
})

// 表单校验规则
const formRules = {
  childId: [{ required: true, message: '请选择儿童', trigger: 'change' }],
  mealType: [{ required: true, message: '请选择餐次', trigger: 'change' }],
  mealTime: [{ required: true, message: '请选择用餐时间', trigger: 'change' }],
  foodItems: [{ required: true, message: '请输入食物项目', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入食用量', trigger: 'blur' }]
}

// 加载儿童列表
const loadChildrenList = async () => {
  try {
    let res;
    if (roleId === 3) {
      // 家长角色
      res = await getMyChildrenService()
      console.log('家长获取儿童列表:', res) // 添加调试日志
    } else {
      // 医生或管理员角色
      res = await getChildListService({ page: 1, pageSize: 100 })
      console.log('医生/管理员获取儿童列表:', res) // 添加调试日志
    }
    
    if (res.code === 0) {
      childrenList.value = roleId === 3 
        ? res.data 
        : res.data.rows
      console.log('处理后的儿童列表:', childrenList.value) // 添加调试日志
      
      // 如果是家长且有孩子，默认选中第一个
      if (roleId === 3 && childrenList.value.length > 0) {
        selectedChildId.value = childrenList.value[0].child_id
        console.log('选中的儿童ID:', selectedChildId.value) // 添加调试日志
        loadRecordList()
      }
    }
  } catch (error) {
    console.error('获取儿童列表失败:', error)
    ElMessage.error('获取儿童列表失败')
  }
}

// 加载记录列表
const loadRecordList = async () => {
  loading.value = true
  try {
    const res = await getDietRecordListService({
      page: currentPage.value,
      pageSize: pageSize.value,
      childId: selectedChildId.value
    })
    
    if (res.code === 0) {
      recordList.value = res.data.rows
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取记录列表失败:', error)
    ElMessage.error('获取记录列表失败')
  } finally {
    loading.value = false
  }
}

// 获取餐次标签类型
const getMealTypeTag = (type) => {
  const map = {
    breakfast: 'success',
    lunch: 'warning',
    dinner: 'danger',
    snack: 'info'
  }
  return map[type] || 'info'
}

// 获取餐次显示文本
const getMealTypeLabel = (type) => {
  const map = {
    breakfast: '早餐',
    lunch: '午餐',
    dinner: '晚餐',
    snack: '点心'
  }
  return map[type] || type
}

// 处理儿童变化
const handleChildChange = () => {
  currentPage.value = 1
  loadRecordList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  loadRecordList()
}

// 处理每页数量变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadRecordList()
}

// 处理添加
const handleAdd = () => {
  dialogTitle.value = '添加饮食记录'
  formData.value = {
    childId: selectedChildId.value,
    mealType: '',
    mealTime: new Date(),
    foodItems: '',
    amount: '',
    notes: ''
  }
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑饮食记录'
  formData.value = {
    recordId: row.record_id,
    childId: row.child_id,
    mealType: row.meal_type,
    mealTime: new Date(row.meal_time),
    foodItems: row.food_items,
    amount: row.amount,
    notes: row.notes || ''
  }
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条记录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await deleteDietRecordService(row.record_id)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      if (recordList.value.length === 1 && currentPage.value > 1) {
        currentPage.value--
      }
      loadRecordList()
    }
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
  
  try {
    await formRef.value.validate()
    
    const submitData = {
      ...formData.value
    }
    
    let res
    if (submitData.recordId) {
      // 编辑
      res = await updateDietRecordService(submitData.recordId, submitData)
    } else {
      // 新增
      res = await addDietRecordService(submitData)
    }
    
    if (res.code === 0) {
      ElMessage.success(submitData.recordId ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadRecordList()
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

// 处理对话框关闭
const handleDialogClose = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
}

// 页面加载时
onMounted(() => {
  loadChildrenList()
  loadRecordList()
})
</script>

<style lang="scss" scoped>
.diet-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
      gap: 10px;

      .header-title {
        font-size: 16px;
        font-weight: bold;
      }

      .header-count {
        margin-left: 10px;
      }
    }

    .header-operations {
      display: flex;
      gap: 10px;

      .search-input {
        width: 200px;
      }
    }
  }

  .table-container {
    padding: 20px;

    :deep(.el-table) {
      margin-bottom: 20px;

      th {
        background-color: #f5f7fa;
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