import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '../api'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  const login = async (username, password) => {
    const data = await authApi.login({ username, password })
    token.value = data.token
    localStorage.setItem('token', data.token)
    userInfo.value = data
    return data
  }

  const getInfo = async () => {
    const data = await authApi.getInfo()
    userInfo.value = data
    return data
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { userInfo, token, login, getInfo, logout }
})
