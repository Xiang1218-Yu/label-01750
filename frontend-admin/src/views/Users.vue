<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="请输入用户名" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" placeholder="全部角色" clearable style="width: 140px">
            <el-option label="管理员" :value="1" />
            <el-option label="宿管" :value="2" />
            <el-option label="学生" :value="3" />
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
          <span>用户列表</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增用户</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="roleTagType[row.role]">{{ roleMap[row.role] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="handleStatus(row)" :disabled="row.role === 1" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该用户?" @confirm="handleDelete(row.id)" :disabled="row.role === 1">
              <template #reference>
                <el-button link type="danger" :disabled="row.role === 1">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination 
          v-model:current-page="query.current" 
          v-model:page-size="query.size" 
          :total="total" 
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper" 
          @size-change="loadData"
          @current-change="loadData" 
        />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="密码" :prop="form.id ? '' : 'password'">
          <el-input v-model="form.password" type="password" :placeholder="form.id ? '不修改请留空' : '请输入密码'" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" :value="1" />
            <el-option label="宿管" :value="2" />
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
import { userApi } from '../api'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()
const query = reactive({ current: 1, size: 10, username: '', role: null })
const form = reactive({ id: null, username: '', password: '', realName: '', phone: '', role: 2 })
const roleMap = { 1: '管理员', 2: '宿管', 3: '学生' }
const roleTagType = { 1: 'danger', 2: 'warning', 3: '' }
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await userApi.page(query)
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

const resetQuery = () => {
  query.username = ''
  query.role = null
  query.current = 1
  loadData()
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, { ...row, password: '' })
  } else {
    Object.assign(form, { id: null, username: '', password: '', realName: '', phone: '', role: 2 })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.id) {
      await userApi.update(form.id, form)
    } else {
      await userApi.create(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

const handleDelete = async (id) => {
  await userApi.delete(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleStatus = async (row) => {
  await userApi.updateStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  loadData()
}

onMounted(loadData)
</script>
