<template>
  <view class="publish-container">
    <u-form :model="formData" ref="formRef">
      <u-form-item>
        <u-textarea
          v-model="formData.content"
          placeholder="写下此刻的想法..."
          :maxlength="500"
          height="300"
        />
      </u-form-item>
      
      <u-form-item label="标签">
        <u-tags-input
          v-model="formData.tagList"
          placeholder="输入标签后点击回车"
          :maxTags="5"
        />
      </u-form-item>
    </u-form>
    
    <view class="submit-btn">
      <u-button type="primary" @click="submitDiary">发布日记</u-button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const formRef = ref(null)
const formData = ref({
  content: '',
  tagList: []
})

const submitDiary = async () => {
  try {
    // 这里替换为实际的API调用
    await uni.$u.http.post('/api/diary/create', formData.value)
    uni.$u.toast('发布成功')
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    uni.$u.toast('发布失败')
  }
}
</script>

<style lang="scss" scoped>
.publish-container {
  padding: 20rpx;
  
  .submit-btn {
    margin-top: 40rpx;
    padding: 0 20rpx;
  }
}
</style> 