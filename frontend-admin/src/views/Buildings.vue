<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="楼栋名称">
          <el-input v-model="query.name" placeholder="请输入楼栋名称" clearable style="width: 180px" />
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
          <span>楼栋列表</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增楼栋</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="楼栋名称" min-width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="gender" label="性别" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === 1 ? 'primary' : 'danger'">{{ row.gender === 1 ? '男' : '女' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="managerName" label="宿管" width="100" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="handleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该楼栋?" @confirm="handleDelete(row.id)">
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
    
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑楼栋' : '新增楼栋'" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" placeholder="请输入楼栋名称" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" /></el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :value="1">男生宿舍</el-radio>
            <el-radio :value="2">女生宿舍</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="宿管">
          <el-select v-model="form.managerId" placeholder="请选择宿管" clearable style="width: 100%">
            <el-option v-for="m in managers" :key="m.id" :label="m.realName" :value="m.id" />
          </el-select>
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
import { buildingApi, userApi } from '../api'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()
const managers = ref([])
const query = reactive({ current: 1, size: 10, name: '' })
const form = reactive({ id: null, name: '', description: '', gender: 1, managerId: null })
const rules = {
  name: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await buildingApi.page(query)
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

const resetQuery = () => { query.name = ''; query.current = 1; loadData() }

const loadManagers = async () => {
  const res = await userApi.page({ current: 1, size: 100, role: 2 })
  managers.value = res.records
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, name: '', description: '', gender: 1, managerId: null })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.id) { await buildingApi.update(form.id, form) } 
    else { await buildingApi.create(form) }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

const handleDelete = async (id) => { await buildingApi.delete(id); ElMessage.success('删除成功'); loadData() }
const handleStatus = async (row) => { await buildingApi.updateStatus(row.id, row.status === 1 ? 0 : 1); ElMessage.success('操作成功'); loadData() }

onMounted(() => { loadData(); loadManagers() })
</script>
