<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="请输入姓名" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="query.studentNo" placeholder="请输入学号" clearable style="width: 150px" />
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
          <span>学生列表</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增学生</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === 1 ? 'primary' : 'danger'" size="small">{{ row.gender === 1 ? '男' : '女' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="college" label="学院" min-width="120" show-overflow-tooltip />
        <el-table-column prop="major" label="专业" min-width="100" show-overflow-tooltip />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="宿舍" width="200">
          <template #default="{ row }">
            <span v-if="row.buildingName">{{ row.buildingName }} {{ row.roomNumber }}室 {{ row.bedNumber }}床</span>
            <el-tag v-else type="info" size="small">未分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="success" @click="openBedDialog(row)">分配床位</el-button>
            <el-popconfirm title="确定删除该学生?" @confirm="handleDelete(row.id)">
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
    
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑学生' : '新增学生'" width="600px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo"><el-input v-model="form.studentNo" placeholder="请输入学号" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name"><el-input v-model="form.name" placeholder="请输入姓名" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender"><el-radio :value="1">男</el-radio><el-radio :value="2">女</el-radio></el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号"><el-input v-model="form.phone" placeholder="请输入手机号" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学院"><el-input v-model="form.college" placeholder="请输入学院" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业"><el-input v-model="form.major" placeholder="请输入专业" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级"><el-input v-model="form.className" placeholder="请输入班级" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入学日期"><el-date-picker v-model="form.enrollDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="bedDialogVisible" title="分配床位" width="500px" :close-on-click-modal="false">
      <div v-if="currentStudent?.bedId" class="current-bed-info">
        <el-alert type="info" :closable="false">
          当前床位：{{ currentStudent.buildingName }} {{ currentStudent.roomNumber }}室 {{ currentStudent.bedNumber }}床
        </el-alert>
        <el-button type="warning" @click="handleUnbind" style="margin-top: 16px">解除绑定</el-button>
      </div>
      <div v-else>
        <el-form label-width="80px">
          <el-form-item label="选择楼栋">
            <el-select v-model="bedForm.buildingId" placeholder="请选择楼栋" @change="loadRooms" style="width: 100%">
              <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="选择房间">
            <el-select v-model="bedForm.roomId" placeholder="请选择房间" @change="loadBeds" style="width: 100%">
              <el-option v-for="r in rooms" :key="r.id" :label="`${r.roomNumber}室 (${r.currentCount}/${r.capacity})`" :value="r.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="选择床位">
            <el-select v-model="bedForm.bedId" placeholder="请选择床位" style="width: 100%">
              <el-option v-for="b in beds" :key="b.id" :label="`${b.bedNumber}床`" :value="b.id" :disabled="b.status === 1" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="bedDialogVisible = false">取消</el-button>
        <el-button v-if="!currentStudent?.bedId" type="primary" @click="handleBind" :disabled="!bedForm.bedId">确定分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { studentApi, buildingApi, roomApi } from '../api'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const bedDialogVisible = ref(false)
const formRef = ref()
const query = reactive({ current: 1, size: 10, name: '', studentNo: '' })
const form = reactive({ id: null, studentNo: '', name: '', gender: 1, phone: '', college: '', major: '', className: '', enrollDate: '' })
const bedForm = reactive({ buildingId: null, roomId: null, bedId: null })
const currentStudent = ref(null)
const buildings = ref([])
const rooms = ref([])
const beds = ref([])
const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await studentApi.page(query)
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

const resetQuery = () => { query.name = ''; query.studentNo = ''; query.current = 1; loadData() }

const openDialog = (row) => {
  if (row) { Object.assign(form, row) } 
  else { Object.assign(form, { id: null, studentNo: '', name: '', gender: 1, phone: '', college: '', major: '', className: '', enrollDate: '' }) }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.id) { await studentApi.update(form.id, form) } 
    else { await studentApi.create(form) }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

const handleDelete = async (id) => { await studentApi.delete(id); ElMessage.success('删除成功'); loadData() }

const openBedDialog = async (row) => {
  currentStudent.value = row
  bedForm.buildingId = null
  bedForm.roomId = null
  bedForm.bedId = null
  rooms.value = []
  beds.value = []
  // 获取所有启用的楼栋（不按性别过滤，因为楼栋可能未设置性别）
  const res = await buildingApi.page({ current: 1, size: 100 })
  buildings.value = res.records.filter(b => b.status === 1)
  bedDialogVisible.value = true
}

const loadRooms = async () => {
  bedForm.roomId = null
  bedForm.bedId = null
  beds.value = []
  const res = await roomApi.page({ current: 1, size: 100, buildingId: bedForm.buildingId })
  rooms.value = res.records.filter(r => r.currentCount < r.capacity)
}

const loadBeds = async () => {
  bedForm.bedId = null
  if (!bedForm.roomId) {
    beds.value = []
    return
  }
  const res = await roomApi.getBeds(bedForm.roomId)
  beds.value = res || []
}

const handleBind = async () => {
  await studentApi.bindBed(currentStudent.value.id, bedForm.bedId)
  ElMessage.success('分配成功')
  bedDialogVisible.value = false
  loadData()
}

const handleUnbind = async () => {
  await studentApi.unbindBed(currentStudent.value.id)
  ElMessage.success('解除成功')
  // 清除当前学生的床位信息，使界面显示分配表单
  currentStudent.value.buildingName = null
  currentStudent.value.roomNumber = null
  currentStudent.value.bedNumber = null
  currentStudent.value.bedId = null
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.current-bed-info { text-align: center; }
:deep(.el-tag) { transition: none !important; }
</style>
