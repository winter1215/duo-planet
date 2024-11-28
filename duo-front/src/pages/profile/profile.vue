<template>
  <view class="profile-container">
    <!-- 未登录状态 -->
    <view v-if="!isLogin" class="not-login">
      <image class="default-avatar" src="/static/default-avatar.png" />
      <view class="login-tips">登录体验更多功能</view>
      <view class="login-btn-group">
        <up-button type="primary" @click="goLogin">登录</up-button>
        <up-button plain @click="goRegister">注册</up-button>
      </view>
    </view>

    <!-- 已登录状态 -->
    <view v-else>
      <!-- 用户信息卡片 -->
      <view class="user-card">
        <view class="user-info">
          <image :src="userInfo.avatar || '/static/default-avatar.png'" class="avatar" @click="changeAvatar" />
          <view class="info-right">
            <view class="nickname">{{ userInfo.nickname }}</view>
            <view class="user-id">ID: {{ userInfo.userId }}</view>
          </view>
        </view>
        <view class="stats-row">
          <view class="stat-item" @click="navigateTo('/pages/diary/my-diary')">
            <text class="num">{{ userInfo.diaryCount }}</text>
            <text class="label">日记</text>
          </view>
          <view class="stat-item" @click="navigateTo('/pages/likes/my-likes')">
            <text class="num">{{ userInfo.likeCount }}</text>
            <text class="label">获赞</text>
          </view>
          <view class="stat-item" @click="navigateTo('/pages/follows/my-follows')">
            <text class="num">{{ userInfo.followCount }}</text>
            <text class="label">关注</text>
          </view>
        </view>
      </view>

      <!-- 功能列表 -->
      <view class="feature-list">
        <up-cell-group>
          <up-cell 
            title="我的收藏" 
            icon="star-fill" 
            isLink 
            @click="navigateTo('/pages/collection/my-collection')"
          />
          <up-cell 
            title="浏览历史" 
            icon="clock-fill" 
            isLink 
            @click="navigateTo('/pages/history/browse-history')"
          />
          <up-cell 
            title="隐私设置" 
            icon="lock" 
            isLink 
            @click="navigateTo('/pages/settings/privacy')"
          />
        </up-cell-group>

        <up-cell-group>
          <up-cell 
            title="意见反馈" 
            icon="chat" 
            isLink 
            @click="navigateTo('/pages/feedback/feedback')"
          />
          <up-cell 
            title="关于我们" 
            icon="info-circle" 
            isLink 
            @click="navigateTo('/pages/about/about')"
          />
        </up-cell-group>
      </view>

      <!-- 退出登录按钮 -->
      <view class="logout-btn">
        <up-button type="error" @click="handleLogout">退出登录</up-button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const isLogin = computed(() => store.getters.isLoggedIn)
const userInfo = ref({
  avatar: '',
  nickname: '',
  userId: '',
  diaryCount: 0,
  likeCount: 0,
  followCount: 0
})

onMounted(async () => {
  if (isLogin.value) {
    await loadUserInfo()
  }
})

const loadUserInfo = async () => {
  try {
    const res = await uni.$u.http.get('/api/user/info')
    userInfo.value = res.data
  } catch (error) {
    uni.showToast({
      title: '获取用户信息失败',
      icon: 'none'
    })
  }
}

const goLogin = () => {
  uni.navigateTo({
    url: '/pages/login/login'
  })
}

const goRegister = () => {
  uni.navigateTo({
    url: '/pages/register/register'
  })
}

const navigateTo = (url) => {
  uni.navigateTo({ url })
}

const changeAvatar = () => {
  uni.chooseImage({
    count: 1,
    success: async (res) => {
      try {
        // 这里需要实现头像上传的逻辑
        const uploadRes = await uploadAvatar(res.tempFilePaths[0])
        userInfo.value.avatar = uploadRes.url
        uni.showToast({
          title: '更新成功',
          icon: 'success'
        })
      } catch (error) {
        uni.showToast({
          title: '更新失败',
          icon: 'none'
        })
      }
    }
  })
}

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        store.dispatch('logout')
        uni.showToast({
          title: '已退出登录',
          icon: 'success'
        })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;

  .not-login {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 100rpx 0;
    background-color: #fff;

    .default-avatar {
      width: 160rpx;
      height: 160rpx;
      border-radius: 50%;
      margin-bottom: 30rpx;
    }

    .login-tips {
      font-size: 28rpx;
      color: #666;
      margin-bottom: 40rpx;
    }

    .login-btn-group {
      display: flex;
      gap: 30rpx;
    }
  }

  .user-card {
    background-color: #fff;
    padding: 40rpx 30rpx;
    margin-bottom: 20rpx;

    .user-info {
      display: flex;
      align-items: center;
      margin-bottom: 40rpx;

      .avatar {
        width: 120rpx;
        height: 120rpx;
        border-radius: 50%;
        margin-right: 30rpx;
      }

      .info-right {
        flex: 1;

        .nickname {
          font-size: 36rpx;
          font-weight: bold;
          margin-bottom: 10rpx;
        }

        .user-id {
          font-size: 24rpx;
          color: #999;
        }
      }
    }

    .stats-row {
      display: flex;
      justify-content: space-around;
      padding: 20rpx 0;
      border-top: 2rpx solid #f5f5f5;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;

        .num {
          font-size: 32rpx;
          font-weight: bold;
          margin-bottom: 8rpx;
        }

        .label {
          font-size: 24rpx;
          color: #666;
        }
      }
    }
  }

  .feature-list {
    margin-bottom: 40rpx;
  }

  .logout-btn {
    padding: 0 30rpx;
  }
}
</style> 