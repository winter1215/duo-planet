<template>
  <view>
      <up-grid
              :border="false"
              @click="navigateTo"
      >
          <up-grid-item
                  v-for="(baseListItem,baseListIndex) in baseList"
                  :key="baseListIndex"
          >
              <up-icon
                      :customStyle="{paddingTop:20+'rpx'}"
                      :name="baseListItem.name"
                      :size="22"
              ></up-icon>
              <text class="grid-text">{{baseListItem.title}}</text>
          </up-grid-item>
      </up-grid>
  </view>
</template>

<script setup>  
import { ref } from 'vue';
  
// 创建响应式数据  
const baseList = ref([  
    {  
        name: 'photo',  
        title: '待办',
        path: '/pages/todo/todo'
    },  
    {  
        name: 'lock',  
        title: '抽奖',
        path: '/pages/lottery/lottery'
    },  
    {  
        name: 'star',  
        title: '星星',
        path: '/pages/star/star'
    },  
]);  

const navigateTo = (index) => {
      // 根据模块名称跳转到对应页面
      const route = baseList.value[index].path;
      uni.navigateTo({
        url: route,
      });
    };
</script>

<style scoped>
.hover-card {
  transition: transform 0.3s;
}

.hover-card:hover {
  transform: scale(1.05);
}
</style>