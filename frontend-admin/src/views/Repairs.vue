<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option v-for="(item, key) in statusMap" :key="key" :label="item.label" :value="Number(key)" />
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
          <span>维修申请列表</span>
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">申请维修</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
        <el-table-column prop="studentName" label="申请人" width="100" />
        <el-table-column label="宿舍" width="150">
          <template #default="{ row }">{{ row.buildingName }} {{ row.roomNumber }}室</template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openProcessDialog(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" 
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" 
          @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
    
    <!-- 处理维修弹窗 -->
    <el-dialog v-model="processDialogVisible" title="处理维修申请" width="600px" :close-on-click-modal="false">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ currentRow?.title }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow?.studentName }}</el-descriptions-item>
        <el-descriptions-item label="宿舍">{{ currentRow?.buildingName }} {{ currentRow?.roomNumber }}室</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentRow?.createTime }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentRow?.description }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <el-form ref="processFormRef" :model="processForm" label-width="80px">
        <el-form-item label="处理状态">
          <el-radio-group v-model="processForm.status">
            <el-radio :value="1">处理中</el-radio>
            <el-radio :value="2">已完成</el-radio>
            <el-radio :value="3">已拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="回复">
          <el-input v-model="processForm.reply" type="textarea" :rows="3" placeholder="请输入处理回复" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleProcess" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 申请维修弹窗 -->
    <el-dialog v-model="createDialogVisible" title="申请维修" width="500px" :close-on-click-modal="false">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="80px">
        <el-form-item label="选择房间" prop="roomId">
          <el-cascader v-model="createForm.cascaderValue" :options="roomOptions" :props="{ value: 'id', label: 'name', children: 'children' }"
            placeholder="请选择楼栋和房间" style="width: 100%" @change="handleRoomChange" />
        </el-form-item>
        <el-form-item label="申请人" prop="studentId">
          <el-select v-model="createForm.studentId" placeholder="请选择申请人" filterable style="width: 100%">
            <el-option v-for="s in roomStudents" :key="s.id" :label="`${s.name} (${s.studentNo})`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="createForm.title" placeholder="请输入维修标题" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="请详细描述维修问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate" :loading="submitLoading">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { repairApi, buildingApi, roomApi, studentApi } from '../api'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const processDialogVisible = ref(false)
const createDialogVisible = ref(false)
const processFormRef = ref()
const createFormRef = ref()
const currentRow = ref(null)
const roomOptions = ref([])
const roomStudents = ref([])
const query = reactive({ current: 1, size: 10, status: null })
const processForm = reactive({ status: 1, reply: '' })
const createForm = reactive({ cascaderValue: [], roomId: null, studentId: null, title: '', description: '' })
const createRules = {
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  studentId: [{ required: true, message: '请选择申请人', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}
const statusMap = {
  0: { label: '待处理', type: 'warning' },
  1: { label: '处理中', type: 'primary' },
  2: { label: '已完成', type: 'success' },
  3: { label: '已拒绝', type: 'danger' }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await repairApi.page(query)
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

const loadRoomOptions = async () => {
  const buildingsRes = await buildingApi.page({ current: 1, size: 100 })
  const buildings = buildingsRes.records.filter(b => b.status === 1)
  const options = []
  for (const b of buildings) {
    const roomsRes = await roomApi.page({ current: 1, size: 100, buildingId: b.id })
    options.push({
      id: `b_${b.id}`,
      name: b.name,
      children: roomsRes.records.map(r => ({ id: r.id, name: `${r.roomNumber}室` }))
    })
  }
  roomOptions.value = options
}

const resetQuery = () => { query.status = null; query.current = 1; loadData() }

const openProcessDialog = (row) => {
  currentRow.value = row
  processForm.status = row.status || 1
  processForm.reply = row.reply || ''
  processDialogVisible.value = true
}

const openCreateDialog = async () => {
  Object.assign(createForm, { cascaderValue: [], roomId: null, studentId: null, title: '', description: '' })
  roomStudents.value = []
  if (roomOptions.value.length === 0) await loadRoomOptions()
  createDialogVisible.value = true
}

const handleRoomChange = async (value) => {
  if (value && value.length === 2) {
    createForm.roomId = value[1]
    createForm.studentId = null
    // 获取该房间的学生
    const beds = await roomApi.getBeds(value[1])
    roomStudents.value = beds.filter(b => b.studentId).map(b => ({ id: b.studentId, name: b.studentName, studentNo: b.studentNo }))
  } else {
    createForm.roomId = null
    roomStudents.value = []
  }
}

const handleProcess = async () => {
  submitLoading.value = true
  try {
    await repairApi.handle(currentRow.value.id, processForm)
    ElMessage.success('处理成功')
    processDialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

const handleCreate = async () => {
  await createFormRef.value.validate()
  submitLoading.value = true
  try {
    await repairApi.create({ roomId: createForm.roomId, studentId: createForm.studentId, title: createForm.title, description: createForm.description })
    ElMessage.success('申请成功')
    createDialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

onMounted(loadData)
</script>
