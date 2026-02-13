<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="操作人">
          <el-input v-model="query.username" placeholder="请输入用户名" clearable @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="模块">
          <el-select v-model="query.module" placeholder="请选择模块" clearable style="width: 160px">
            <el-option label="用户管理" value="用户管理" />
            <el-option label="楼栋管理" value="楼栋管理" />
            <el-option label="房间管理" value="房间管理" />
            <el-option label="学生管理" value="学生管理" />
            <el-option label="维修管理" value="维修管理" />
            <el-option label="访客管理" value="访客管理" />
            <el-option label="公告管理" value="公告管理" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="operation" label="操作" width="150" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column prop="method" label="方法" show-overflow-tooltip />
      </el-table>

      <el-pagination
        v-model:current-page="query.current"
        v-model:page-size="query.size"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { operationLogApi } from '../api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10, username: '', module: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await operationLogApi.page(query)
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.username = ''
  query.module = ''
  query.current = 1
  loadData()
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.page-container {
  padding: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
