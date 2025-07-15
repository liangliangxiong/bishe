<template>
  <div class="dashboard-container">
    <!-- 欢迎信息 -->
    <el-row :gutter="20" class="welcome-section">
      <el-col :span="24">
        <el-card>
          <div class="welcome-content">
            <h2>欢迎回来，{{ tokenStore.username }}</h2>
            <p>今天是 {{ currentDate }}，祝您使用愉快！</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据概览 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="6">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>儿童总数</span>
            </div>
          </template>
          <div class="stats-value">{{ childrenCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>本月体检记录</span>
            </div>
          </template>
          <div class="stats-value">{{ monthlyCheckCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>待接种疫苗</span>
            </div>
          </template>
          <div class="stats-value">{{ pendingVaccinations }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>异常记录</span>
            </div>
          </template>
          <div class="stats-value">{{ abnormalRecords }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近记录 -->
    <el-row :gutter="20" class="recent-section">
      <el-col :span="12">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span>最近体温记录</span>
              <el-button v-if="tokenStore.roleId === 1" class="more-btn" text @click="goToTemperature">
                查看更多
              </el-button>
            </div>
          </template>
          <el-table :data="recentTemperatures" style="width: 100%" v-loading="loading.temperature">
            <el-table-column prop="childName" label="儿童姓名" width="120" />
            <el-table-column prop="temperature" label="体温" width="100">
              <template #default="{ row }">
                <span :class="{ 'text-warning': row.temperature >= 37.3 }">
                  {{ row.temperature }}°C
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="measureTime" label="测量时间">
              <template #default="{ row }">
                {{ formatDateTime(row.measureTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span>最近就医记录</span>
              <el-button v-if="tokenStore.roleId === 1" class="more-btn" text @click="goToMedical">
                查看更多
              </el-button>
            </div>
          </template>
          <el-table :data="recentMedicalRecords" style="width: 100%" v-loading="loading.medical">
            <el-table-column prop="childName" label="儿童姓名" width="120" />
            <el-table-column prop="hospitalName" label="医院" width="180" />
            <el-table-column prop="visitDate" label="就诊日期">
              <template #default="{ row }">
                {{ formatDate(row.visitDate) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待办提醒 -->
    <el-row :gutter="20" class="todo-section">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待办提醒</span>
            </div>
          </template>
          <el-table :data="todoList" style="width: 100%" v-loading="loading.todo">
            <el-table-column prop="childName" label="儿童姓名" width="120" />
            <el-table-column prop="type" label="类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getTagType(row.type)">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容" />
            <el-table-column prop="dueDate" label="截止日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.dueDate) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useTokenStore } from '@/stores/token'
import { formatDate, formatDateTime } from '@/utils/format'
import { getMyChildrenService } from '@/api/child'
import { getTemperatureRecordListService } from '@/api/temperature'
import { getMedicalRecordListService } from '@/api/medicalRecords'
import { getVaccinationListService } from '@/api/vaccinations'
import { getDietRecordListService } from '@/api/dietRecords'

const router = useRouter()
const tokenStore = useTokenStore()

// 状态数据
const childrenCount = ref(0)
const monthlyCheckCount = ref(0)
const pendingVaccinations = ref(0)
const abnormalRecords = ref(0)

const recentTemperatures = ref([])
const recentMedicalRecords = ref([])
const todoList = ref([])

const loading = ref({
  temperature: false,
  medical: false,
  todo: false
})

// 计算属性
const currentDate = computed(() => {
  return formatDate(new Date())
})

// 获取儿童列表
const fetchChildrenData = async () => {
  try {
    const res = await getMyChildrenService()
    if (res.code === 0) {
      childrenCount.value = res.data.length
    }
  } catch (error) {
    console.error('获取儿童数据失败:', error)
  }
}

// 获取最近体温记录
const fetchRecentTemperatures = async () => {
  loading.value.temperature = true
  try {
    const res = await getTemperatureRecordListService({
      page: 1,
      pageSize: 5
    })
    if (res.code === 0) {
      recentTemperatures.value = res.data.records
      // 统计异常记录
      const abnormalTemp = res.data.records.filter(record => record.temperature >= 37.3)
      abnormalRecords.value += abnormalTemp.length
    }
  } catch (error) {
    console.error('获取体温记录失败:', error)
  } finally {
    loading.value.temperature = false
  }
}

// 获取最近就医记录
const fetchRecentMedicalRecords = async () => {
  loading.value.medical = true
  try {
    const res = await getMedicalRecordListService({
      page: 1,
      pageSize: 5
    })
    if (res.code === 0) {
      recentMedicalRecords.value = res.data.records
      // 统计本月体检记录
      const currentMonth = new Date().getMonth() + 1
      monthlyCheckCount.value = res.data.records.filter(record => {
        const recordMonth = new Date(record.visitDate).getMonth() + 1
        return recordMonth === currentMonth
      }).length
    }
  } catch (error) {
    console.error('获取就医记录失败:', error)
  } finally {
    loading.value.medical = false
  }
}

// 获取待办事项
const fetchTodoList = async () => {
  loading.value.todo = true
  try {
    // 获取待接种疫苗
    const vaccineRes = await getVaccinationListService({
      status: 'pending'
    })
    if (vaccineRes.code === 0) {
      const vaccineItems = vaccineRes.data.records.map(item => ({
        childName: item.childName,
        type: '疫苗接种',
        content: `需要接种 ${item.vaccineName}`,
        dueDate: item.plannedDate
      }))
      todoList.value.push(...vaccineItems)
      pendingVaccinations.value = vaccineItems.length
    }

    // 获取饮食提醒
    const dietRes = await getDietRecordListService({
      status: 'pending'
    })
    if (dietRes.code === 0) {
      const dietItems = dietRes.data.records.map(item => ({
        childName: item.childName,
        type: '饮食提醒',
        content: item.notes,
        dueDate: item.planDate
      }))
      todoList.value.push(...dietItems)
    }
  } catch (error) {
    console.error('获取待办事项失败:', error)
  } finally {
    loading.value.todo = false
  }
}

// 标签类型
const getTagType = (type) => {
  const types = {
    '疫苗接种': 'danger',
    '饮食提醒': 'warning',
    '体检提醒': 'info'
  }
  return types[type] || 'info'
}

// 页面跳转
const goToTemperature = () => {
  router.push('/medical/temperature')
}

const goToMedical = () => {
  router.push('/medical/visits')
}

// 初始化
onMounted(() => {
  fetchChildrenData()
  fetchRecentTemperatures()
  fetchRecentMedicalRecords()
  fetchTodoList()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.welcome-section {
  margin-bottom: 20px;
}

.welcome-content {
  text-align: center;
}

.stats-section {
  margin-bottom: 20px;
}

.stats-card {
  text-align: center;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.recent-section {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-warning {
  color: #E6A23C;
}

.todo-section :deep(.el-card__body) {
  padding: 0;
}

:deep(.el-table) {
  margin: 0;
}
</style>