<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="楼栋">
          <el-select v-model="query.buildingId" placeholder="全部楼栋" clearable style="width: 150px">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="query.roomNumber" placeholder="请输入房间号" clearable style="width: 150px" />
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
          <span>房间列表</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增房间</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="buildingName" label="楼栋" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column label="入住情况" width="150">
          <template #default="{ row }">
            <el-progress :percentage="Math.round(row.currentCount / row.capacity * 100)" :color="getProgressColor(row)" :duration="0" />
            <span class="progress-text">{{ row.currentCount }} / {{ row.capacity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="handleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="床位详情" min-width="300">
          <template #default="{ row }">
            <div class="bed-list">
              <el-tag v-for="bed in row.beds" :key="bed.id" :type="bed.status === 1 ? 'success' : 'info'" class="bed-tag">
                {{ bed.bedNumber }}床: {{ bed.studentName || '空闲' }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该房间?" @confirm="handleDelete(row.id)">
              <template #reference><el-button link type="danger">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" 
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" 
          @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑房间' : '新增房间'" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="form.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="10" style="width: 100%" />
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
import { ref, reactive, onMounted } from 'vue'
import { roomApi, buildingApi } from '../api'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()
const buildings = ref([])
const query = reactive({ current: 1, size: 10, buildingId: null, roomNumber: '' })
const form = reactive({ id: null, buildingId: null, roomNumber: '', capacity: 4 })
const rules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }]
}

const getProgressColor = (row) => {
  const rate = row.currentCount / row.capacity
  if (rate >= 1) return '#67c23a'
  if (rate >= 0.5) return '#e6a23c'
  return '#909399'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await roomApi.page(query)
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

const loadBuildings = async () => {
  const res = await buildingApi.page({ current: 1, size: 100 })
  buildings.value = res.records
}

const resetQuery = () => { query.buildingId = null; query.roomNumber = ''; query.current = 1; loadData() }

const openDialog = (row) => {
  if (row) { Object.assign(form, row) } 
  else { Object.assign(form, { id: null, buildingId: null, roomNumber: '', capacity: 4 }) }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.id) { await roomApi.update(form.id, form) } 
    else { await roomApi.create(form) }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

const handleDelete = async (id) => { await roomApi.delete(id); ElMessage.success('删除成功'); loadData() }
const handleStatus = async (row) => { await roomApi.updateStatus(row.id, row.status === 1 ? 0 : 1); ElMessage.success('操作成功'); loadData() }

onMounted(() => { loadData(); loadBuildings() })
</script>

<style scoped>
.progress-text { font-size: 12px; color: #909399; margin-left: 8px; }
.bed-list { display: flex; flex-wrap: wrap; gap: 8px; }
.bed-tag { margin: 0; transition: none !important; }
</style>
