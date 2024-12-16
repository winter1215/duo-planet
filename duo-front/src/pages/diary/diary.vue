<template>
  <view class="diary-container">
    <u-list @scrolltolower="loadMore">
      <u-list-item
        v-for="item in diaryList"
        :key="item.id"
        @click="goDetail(item.id)"
      >
        <view class="diary-item">
          <view class="diary-content">{{ item.content }}</view>
          <view class="diary-info">
            <view class="diary-time">{{ formatTime(item.createTime) }}</view>
            <view class="diary-tags">
              <u-tag
                v-for="tag in item.tagList"
                :key="tag"
                :text="tag"
                type="primary"
                size="mini"
              />
            </view>
          </view>
        </view>
      </u-list-item>
    </u-list>

    <view class="publish-btn" @click="goPublish">
      <u-icon name="plus" color="#FFFFFF" size="28"></u-icon>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { formatDate } from "@/utils/date";
import request from "@/utils/request";

const diaryList = ref([]);
const page = ref(1);
const pageSize = ref(10);
const hasMore = ref(true);
const total = ref(0);

const loadDiaryList = async () => {
  try {
    const res = await request.post("/diary/my/list/page/vo", {
      page: page.value,
      pageSize: pageSize.value,
    });

    total.value = res.total;
    hasMore.value = diaryList.value.length < total.value;

    if (page.value === 1) {
      diaryList.value = res.records || [];
    } else if (res.records && res.records.length > 0) {
      diaryList.value = [...diaryList.value, ...res.records];
    }
  } catch (error) {
    uni.showToast({
      title: "获取日记列表失败",
      icon: "none",
    });
  }
};

const loadMore = () => {
  if (!hasMore.value) {
    uni.showToast({
      title: "没有更多数据了",
      icon: "none",
    });
    return;
  }

  if (diaryList.value.length >= total.value) {
    hasMore.value = false;
    return;
  }

  page.value++;
  loadDiaryList();
};

const goDetail = (id) => {
  uni.navigateTo({
    url: `/pages/diary/detail?id=${id}`,
  });
};

const goPublish = () => {
  uni.navigateTo({
    url: "/pages/diary/publish",
  });
};

const formatTime = (time) => {
  return formatDate(new Date(time), "yyyy-MM-dd hh:mm");
};

onMounted(() => {
  loadDiaryList();
});
</script>

<style lang="scss" scoped>
.diary-container {
  padding: 20rpx;

  .diary-item {
    background-color: #ffffff;
    padding: 20rpx;
    margin-bottom: 20rpx;
    border-radius: 12rpx;

    .diary-content {
      font-size: 28rpx;
      line-height: 1.6;
      margin-bottom: 16rpx;
    }

    .diary-info {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .diary-time {
        font-size: 24rpx;
        color: #909399;
      }

      .diary-tags {
        display: flex;
        gap: 10rpx;
      }
    }
  }

  .publish-btn {
    position: fixed;
    right: 40rpx;
    bottom: 120rpx;
    width: 100rpx;
    height: 100rpx;
    background-color: #5098ff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4rpx 16rpx rgba(80, 152, 255, 0.3);
  }
}
</style>
