<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="访客姓名">
          <el-input v-model="query.visitorName" placeholder="请输入访客姓名" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="来访中" :value="0" />
            <el-option label="已离开" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadData">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>访客记录</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">登记访客</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="visitorName" label="访客姓名" width="100" />
        <el-table-column prop="visitorPhone" label="访客电话" width="130" />
        <el-table-column prop="studentName" label="被访学生" width="100" />
        <el-table-column label="被访宿舍" width="150">
          <template #default="{ row }">{{ row.buildingName }} {{ row.roomNumber }}室</template>
        </el-table-column>
        <el-table-column prop="reason" label="来访事由" min-width="150" show-overflow-tooltip />
        <el-table-column prop="visitTime" label="来访时间" width="180" />
        <el-table-column prop="leaveTime" label="离开时间" width="180" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'">{{ row.status === 0 ? '来访中' : '已离开' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="primary" @click="handleLeave(row.id)">登记离开</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" 
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" 
          @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" title="登记访客" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="访客姓名" prop="visitorName">
          <el-input v-model="form.visitorName" placeholder="请输入访客姓名" />
        </el-form-item>
        <el-form-item label="访客电话" prop="visitorPhone">
          <el-input v-model="form.visitorPhone" placeholder="请输入访客电话" maxlength="11" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="被访学生" prop="studentId">
          <el-select v-model="form.studentId" placeholder="请选择被访学生" filterable style="width: 100%">
            <el-option v-for="s in students" :key="s.id" :label="`${s.name} (${s.studentNo})`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="来访事由" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="2" placeholder="请输入来访事由" />
        </el-form-item>
        <el-form-item label="来访时间" prop="visitTime">
          <el-date-picker v-model="form.visitTime" type="datetime" placeholder="选择时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { visitorApi, studentApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const isStudent = computed(() => userInfo.value?.role === 3)

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()
const students = ref([])
const query = reactive({ current: 1, size: 10, visitorName: '', status: null })
const form = reactive({ visitorName: '', visitorPhone: '', idCard: '', studentId: null, reason: '', visitTime: '' })
const rules = {
  visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
  visitorPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }],
  idCard: [{ pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '请输入正确的身份证号格式', trigger: 'blur' }],
  studentId: [{ required: true, message: '请选择被访学生', trigger: 'change' }],
  reason: [{ required: true, message: '请输入来访事由', trigger: 'blur' }],
  visitTime: [{ required: true, message: '请选择来访时间', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = isStudent.value ? await visitorApi.myPage(query) : await visitorApi.page(query)
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

const loadStudents = async () => {
  if (isStudent.value) return // 学生不需要加载学生列表
  const res = await studentApi.page({ current: 1, size: 1000 })
  students.value = res.records
}

const resetQuery = () => { query.visitorName = ''; query.status = null; query.current = 1; loadData() }

const openDialog = () => {
  Object.assign(form, { visitorName: '', visitorPhone: '', idCard: '', studentId: null, reason: '', visitTime: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await visitorApi.create(form)
    ElMessage.success('登记成功')
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

const handleLeave = async (id) => {
  await visitorApi.leave(id)
  ElMessage.success('登记离开成功')
  loadData()
}

onMounted(() => { loadData(); loadStudents() })
</script>
