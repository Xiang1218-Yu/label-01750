<template>
  <div class="login-container">
    <!-- 动态背景 -->
    <div class="bg-wrapper">
      <div class="bg-gradient"></div>
      <div class="bg-pattern"></div>
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
      </div>
    </div>
    
    <!-- 登录卡片 -->
    <div class="login-wrapper">
      <div class="login-card">
        <!-- 左侧装饰 -->
        <div class="card-left">
          <div class="brand-area">
            <div class="brand-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M3 21h18M3 7v14M21 7v14M6 11h.01M6 15h.01M6 19h.01M10 11h.01M10 15h.01M10 19h.01M14 11h.01M14 15h.01M14 19h.01M18 11h.01M18 15h.01M18 19h.01M12 3l9 4M12 3L3 7"/>
              </svg>
            </div>
            <h1>宿舍管理系统</h1>
            <p>Dormitory Management System</p>
          </div>
          <div class="features">
            <div class="feature-item">
              <span class="dot"></span>
              <span>智能化宿舍管理</span>
            </div>
            <div class="feature-item">
              <span class="dot"></span>
              <span>便捷维修申请</span>
            </div>
            <div class="feature-item">
              <span class="dot"></span>
              <span>访客登记管理</span>
            </div>
          </div>
        </div>
        
        <!-- 右侧表单 -->
        <div class="card-right">
          <div class="form-header">
            <h2>欢迎登录</h2>
            <p>请输入您的账号信息</p>
          </div>
          
          <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名" 
                prefix-icon="User" 
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码" 
                prefix-icon="Lock" 
                size="large" 
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                :loading="loading" 
                @click="handleLogin"
                class="login-btn"
              >
                {{ loading ? '登录中...' : '登 录' }}
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="form-footer">
          </div>
        </div>
      </div>
      
      <div class="copyright">
        © 2026 宿舍管理系统 All Rights Reserved
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

// 背景层
.bg-wrapper {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 50%, #3d7ab5 100%);
}

.bg-pattern {
  position: absolute;
  inset: 0;
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(255,255,255,0.02) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(255,255,255,0.02) 0%, transparent 50%);
}

.floating-shapes {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(61, 122, 181, 0.15) 0%, rgba(45, 90, 135, 0.1) 100%);
  animation: float 20s infinite ease-in-out;
}

.shape-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  left: -100px;
}

.shape-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  right: -50px;
  animation-delay: -5s;
}

.shape-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 10%;
  animation-delay: -10s;
}

.shape-4 {
  width: 150px;
  height: 150px;
  top: 20%;
  right: 15%;
  animation-delay: -15s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -30px) scale(1.05); }
  50% { transform: translate(0, 20px) scale(1); }
  75% { transform: translate(-20px, -10px) scale(0.95); }
}

// 登录区域
.login-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-card {
  display: flex;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 
    0 25px 50px -12px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.1);
  overflow: hidden;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 左侧装饰区
.card-left {
  width: 320px;
  padding: 48px 40px;
  background: linear-gradient(135deg, #2d5a87 0%, #3d7ab5 100%);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.brand-area {
  text-align: center;
  margin-bottom: 48px;
}

.brand-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 20px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  
  svg {
    width: 40px;
    height: 40px;
  }
}

.brand-area h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  letter-spacing: 2px;
}

.brand-area p {
  font-size: 12px;
  opacity: 0.8;
  letter-spacing: 1px;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  opacity: 0.9;
  
  .dot {
    width: 8px;
    height: 8px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 50%;
  }
}

// 右侧表单区
.card-right {
  width: 400px;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: 32px;
  
  h2 {
    font-size: 28px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 8px;
  }
  
  p {
    font-size: 14px;
    color: #64748b;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.login-btn {
  width: 100%;
}

.form-footer {
  display: none;
}

.copyright {
  margin-top: 32px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

// 响应式
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    margin: 20px;
  }
  
  .card-left {
    width: 100%;
    padding: 32px;
  }
  
  .card-right {
    width: 100%;
    padding: 32px;
  }
}
</style>
