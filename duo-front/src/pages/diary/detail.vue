<template>
  <view class="detail-container">
    <view class="diary-content">{{ diaryDetail.content }}</view>
    <view class="diary-info">
      <view class="diary-time">{{ formatTime(diaryDetail.createTime) }}</view>
      <view class="diary-tags">
        <!-- <u-tag 
          v-for="tag in diaryDetail.tagList" 
          :key="tag" 
          :text="tag" 
          type="primary" 
          size="mini" 
        /> -->
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { formatDate } from '@/utils/date'
import request from "@/utils/request";


const diaryDetail = ref({})

const getDiaryDetail = async (id) => {
  try {
    // 这里替换为实际的API调用
    const res = await request.get(`/diary/get/vo`, {id})
    diaryDetail.value = res
  } catch (error) {
    uni.$u.toast('获取日记详情失败')
  }
}

const formatTime = (time) => {
  return formatDate(new Date(time), 'yyyy-MM-dd hh:mm')
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const id = currentPage.options.id
  getDiaryDetail(id)
})
</script>

<style lang="scss" scoped>
.detail-container {
  padding: 30rpx;
  
  .diary-content {
    font-size: 30rpx;
    line-height: 1.8;
    margin-bottom: 30rpx;
  }
  
  .diary-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .diary-time {
      font-size: 26rpx;
      color: #909399;
    }
    
    .diary-tags {
      display: flex;
      gap: 10rpx;
    }
  }
}
</style> 