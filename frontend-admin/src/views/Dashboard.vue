<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col :span="6" v-for="item in statCards" :key="item.key">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats[item.key] || 0 }}</div>
              <div class="stat-label">{{ item.label }}</div>
            </div>
            <el-icon class="stat-icon" :style="{ color: item.color }">
              <component :is="item.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>最新公告</span>
          <el-button link type="primary" @click="$router.push('/announcements')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="announcements" stripe>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="typeMap[row.type]?.type">{{ typeMap[row.type]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
      </el-table>
      <el-empty v-if="!announcements.length" description="暂无公告" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { dashboardApi, announcementApi } from '../api'

const stats = ref({})
const announcements = ref([])

const statCards = [
  { key: 'buildingCount', label: '楼栋数', icon: 'OfficeBuilding', color: '#409EFF' },
  { key: 'roomCount', label: '房间数', icon: 'House', color: '#67C23A' },
  { key: 'studentCount', label: '学生数', icon: 'User', color: '#E6A23C' },
  { key: 'pendingRepairs', label: '待处理维修', icon: 'Tools', color: '#F56C6C' }
]

const typeMap = {
  1: { label: '通知', type: 'primary' },
  2: { label: '规章', type: 'warning' },
  3: { label: '活动', type: 'success' }
}

onMounted(async () => {
  stats.value = await dashboardApi.stats()
  announcements.value = await announcementApi.latest(5)
})
</script>

<style lang="scss" scoped>
.stat-card {
  margin-bottom: 16px;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stat-icon {
  font-size: 48px;
  opacity: 0.8;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
