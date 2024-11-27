<template>
  <view class="app-container">
    <view class="page-content">
      <router-view></router-view>
    </view>
    
    <up-tabbar
      :value="current"
      :fixed="true"
      :border="true"
      @change="tabChange"
      :safeAreaInsetBottom="true"
    >
      <up-tabbar-item text="首页" icon="home" ></up-tabbar-item>
      <up-tabbar-item text="放映厅" icon="photo" ></up-tabbar-item>
      <up-tabbar-item text="直播" icon="play-right" ></up-tabbar-item>
      <up-tabbar-item text="我的" icon="account" ></up-tabbar-item>
  </up-tabbar>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const current = ref(0)

const tabList = [
  {
    pagePath: '/pages/HomePage',
    text: '首页',
    iconPath: '/static/tabbar/home.png',
    selectedIconPath: '/static/tabbar/home-active.png'
  },
  {
    pagePath: '/pages/diary/diary',
    text: '日记',
    iconPath: '/static/tabbar/diary.png',
    selectedIconPath: '/static/tabbar/diary-active.png'
  }
]

const tabChange = (index) => {
  current.value = index
  uni.switchTab({
    url: tabList[index].pagePath
  })
}
</script>

<style lang="scss">
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  
  .page-content {
    flex: 1;
    overflow-y: auto;
  }
}

// 为了避免底部安全区域的遮挡
.safe-area-inset-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}
</style>
