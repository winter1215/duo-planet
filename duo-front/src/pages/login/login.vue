<template>
  <view class="login-container">
    <view class="login-box">
      <view class="logo">
        <image src="/static/logo.png" mode="aspectFit"></image>
      </view>
      <view class="form-item">
        <u-input
          v-model="loginForm.userAccount"
          placeholder="请输入用户名"
          prefixIcon="account"
          border="none"
        />
      </view>
      <view class="form-item">
        <u-input
          v-model="loginForm.userPassword"
          type="password"
          placeholder="请输入密码"
          prefixIcon="lock"
          border="none"
        />
      </view>
      <view class="form-item">
        <u-button type="primary" @click="handleLogin" :loading="loading"
          >登录</u-button
        >
      </view>
      <view class="actions">
        <text @click="goToRegister">还没有账号？立即注册</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from "vue";
import { useStore } from "vuex";
import request from "@/utils/request";

const store = useStore();
const loading = ref(false);

const handleLogin = async () => {
  const loginForm = {
    userAccount: store.state.loginForm.userAccount,
    userPassword: store.state.loginForm.userPassword,
  };

  if (!loginForm.userAccount || !loginForm.userPassword) {
    uni.showToast({
      title: "请输入用户名和密码",
      icon: "none",
    });
    return;
  }

  loading.value = true;
  try {
    const res = await request.post("/api/user/login", loginForm);
    store.commit('setToken', res.token);
    uni.showToast({
      title: "登录成功",
      icon: "success",
    });
    setTimeout(() => {
      uni.switchTab({
        url: "/pages/HomePage",
      });
    }, 1500);
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const goToRegister = () => {
  uni.navigateTo({
    url: "/pages/register/register",
  });
};
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  .login-box {
    width: 100%;
    background-color: #fff;
    border-radius: 16rpx;
    padding: 40rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

    .logo {
      text-align: center;
      margin-bottom: 60rpx;

      image {
        width: 180rpx;
        height: 180rpx;
      }
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
