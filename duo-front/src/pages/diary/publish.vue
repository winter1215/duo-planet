<template>
  <view class="publish-container">
    <up-form :model="formData" ref="formRef">
      <up-form-item>
        <up-textarea
          v-model="formData.content"
          placeholder="写下此刻的想法..."
          :maxlength="500"
          height="300"
        />
      </up-form-item>

      <up-form-item label="标签">
        <up-input
          v-model="formData.tag"
          placeholder="输入标签后点击添加"
          border="surround"
        >
          <template #suffix>
            <up-button
              type="primary"
              size="mini"
              @click="addTag"
              :disabled="formData.tagList.length >= 5"
              >添加</up-button
            >
          </template>
        </up-input>
      </up-form-item>

      <view class="tags-container" v-if="formData.tagList.length">
        <up-tag
          v-for="(tag, index) in formData.tagList"
          :key="index"
          :text="tag"
          type="primary"
          closeable
          @close="removeTag(index)"
          class="tag-item"
        />
      </view>

      <up-form-item label="可见性">
        <up-radio-group v-model="formData.visiable">
          <up-radio
            v-for="item in visibleOptions"
            :key="item.value"
            :label="item.label"
            :name="item.value"
          ></up-radio>
        </up-radio-group>
      </up-form-item>
    </up-form>

    <view class="submit-btn">
      <up-button type="primary" @click="submitDiary">发布日记</up-button>
    </view>
  </view>
</template>

<script setup>
import { ref } from "vue";
import request from "@/utils/request";

const formRef = ref(null);
const formData = ref({
  content: "",
  tag: "",
  tagList: [],
  visiable: 0,
});

const visibleOptions = [
  { label: "公开", value: 0 },
  { label: "仅自己可见", value: 1 },
];

// 添加标签
const addTag = () => {
  if (!formData.value.tag.trim()) {
    uni.showToast({
      title: "标签不能为空",
      icon: "none",
    });
    return;
  }

  if (formData.value.tagList.length >= 5) {
    uni.showToast({
      title: "最多添加5个标签",
      icon: "none",
    });
    return;
  }

  if (formData.value.tagList.includes(formData.value.tag.trim())) {
    uni.showToast({
      title: "标签已存在",
      icon: "none",
    });
    return;
  }

  formData.value.tagList.push(formData.value.tag.trim());
  formData.value.tag = "";
};

// 移除标签
const removeTag = (index) => {
  formData.value.tagList.splice(index, 1);
};

const submitDiary = async () => {
  if (!formData.value.content.trim()) {
    uni.showToast({
      title: "请输入日记内容",
      icon: "none",
    });
    return;
  }

  try {
    const submitData = {
      content: formData.value.content.trim(),
      tags: formData.value.tagList,
      visiable: formData.value.visiable,
    };

    await request.post("/api/diary/add", submitData);

    uni.showToast({
      title: "发布成功",
      icon: "success",
    });

    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  } catch (error) {
    console.error("发布日记失败:", error);
  }
};
</script>

<style lang="scss" scoped>
.publish-container {
  padding: 30rpx;
  background-color: #f5f5f5;
  min-height: 100vh;

  .tags-container {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
    margin: 20rpx 0;

    .tag-item {
      margin-bottom: 10rpx;
    }
  }

  .submit-btn {
    margin-top: 40rpx;
    padding: 0 20rpx;
  }
}
</style>
