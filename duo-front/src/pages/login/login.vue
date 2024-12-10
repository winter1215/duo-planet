<template>
  <view class="login-container">
    <view class="login-box">
      <view class="title">欢迎登录</view>
      
      <up-form :model="formData" ref="formRef">
        <up-form-item>
          <up-input
            v-model="formData.userAccount"
            placeholder="请输入账号"
            prefixIcon="account"
            border="surround"
          />
        </up-form-item>
        
        <up-form-item>
          <up-input
            v-model="formData.userPassword"
            type="password"
            placeholder="请输入密码"
            prefixIcon="lock"
            border="surround"
          />
        </up-form-item>
      </up-form>
      
      <view class="btn-group">
        <up-button 
          type="primary" 
          block 
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </up-button>
      </view>
      
      <view class="action-links">
        <text @click="goRegister">注册账号</text>
        <text @click="goForgetPassword">忘记密码</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useStore } from 'vuex'
import request from '@/utils/request'

const store = useStore()
const formRef = ref(null)
const loading = ref(false)

const formData = ref({
  userAccount: '',
  userPassword: ''
})

const handleLogin = async () => {
  if (!formData.value.userAccount || !formData.value.userPassword) {
    uni.showToast({
      title: '请输入账号和密码',
      icon: 'none'
    })
    return
  }

  loading.value = true
  try {
    const res = await request.post('/user/login', {
      userAccount: formData.value.userAccount,
      userPassword: formData.value.userPassword
    })
    
    // 存储登录信息
    store.dispatch('login', {
      token: res.token,
      userInfo: res.userInfo
    })

    uni.showToast({
      title: '登录成功',
      icon: 'success'
    })

    // 延迟跳转
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/HomePage'
      })
    }, 1500)
  } catch (error) {
    uni.showToast({
      title: error.message || '登录失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

const goRegister = () => {
  uni.navigateTo({
    url: '/pages/register/register'
  })
}

const goForgetPassword = () => {
  uni.navigateTo({
    url: '/pages/forget-password/forget-password'
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  width: 100vw;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  padding: 0 40rpx;
  position: fixed;
  top: 0;
  left: 0;
  
  .login-box {
    width: 100%;
    max-width: 600rpx;
    background-color: #fff;
    border-radius: 20rpx;
    padding: 40rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
    
    .title {
      font-size: 40rpx;
      font-weight: bold;
      text-align: center;
      margin-bottom: 60rpx;
    }
    
    .btn-group {
      margin-top: 60rpx;
    }
    
    .action-links {
      display: flex;
      justify-content: space-between;
      margin-top: 30rpx;
      font-size: 28rpx;
      color: #666;
      
      text {
        padding: 10rpx;
        
        &:active {
          opacity: 0.7;
        }
      }
    }
  }
}

:deep(uni-page-body),
:deep(uni-page-wrapper) {
  height: 100vh !important;
  overflow: hidden !important;
}
</style>
