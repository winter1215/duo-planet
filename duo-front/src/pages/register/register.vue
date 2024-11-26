<template>
  <view class="register-container">
    <view class="register-box">
      <view class="title">注册账号</view>
      <view class="form-item">
        <up-input
          v-model="registerForm.username"
          placeholder="请输入用户名"
          prefixIcon="account"
          border="none"
        />
      </view>
      <view class="form-item">
        <up-input
          v-model="registerForm.password"
          type="password"
          placeholder="请输入密码"
          prefixIcon="lock"
          border="none"
        />
      </view>
      <view class="form-item">
        <up-input
          v-model="registerForm.confirmPassword"
          type="password"
          placeholder="请确认密码"
          prefixIcon="lock"
          border="none"
        />
      </view>
      <view class="form-item">
        <up-button type="primary" @click="handleRegister" :loading="loading">注册</up-button>
      </view>
      <view class="actions">
        <text @click="goToLogin">已有账号？立即登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password || !registerForm.confirmPassword) {
    uni.showToast({
      title: '请填写完整信息',
      icon: 'none'
    })
    return
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    uni.showToast({
      title: '两次输入的密码不一致',
      icon: 'none'
    })
    return
  }

  loading.value = true
  try {
    await request.post('/api/register', {
      username: registerForm.username,
      password: registerForm.password
    })
    uni.showToast({
      title: '注册成功',
      icon: 'success'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/login/login'
  })
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  .register-box {
    width: 100%;
    background-color: #fff;
    border-radius: 16rpx;
    padding: 40rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

    .title {
      font-size: 36rpx;
      font-weight: bold;
      text-align: center;
      margin-bottom: 60rpx;
      color: #333;
    }

    .form-item {
      margin-bottom: 30rpx;
    }

    .actions {
      text-align: center;
      margin-top: 30rpx;
      color: #666;
      font-size: 28rpx;

      text {
        color: #2979ff;
      }
    }
  }
}
</style> 