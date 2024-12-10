<template>
  <view class="lottery-container">
    <!-- 顶部信息 -->
    <view class="user-info">
      <view class="points">
        <text class="label">我的积分</text>
        <text class="num">{{ userPoints }}</text>
      </view>
      <view class="draw-times">
        <text class="label">剩余抽奖次数</text>
        <text class="num">{{ drawTimes }}</text>
      </view>
    </view>

    <!-- 抽奖区域 -->
    <view class="lottery-box">
      <view class="lottery-grid" :class="{ 'is-drawing': isDrawing }">
        <view 
          v-for="(item, index) in prizeList" 
          :key="index"
          :class="[
            'grid-item',
            { active: currentIndex === index },
            { 'is-center': index === 4 }
          ]"
        >
          <view class="item-inner">
            <template v-if="index !== 4">
              <view class="prize-content">
                <image :src="item.image" class="prize-img" mode="aspectFit" />
                <text class="prize-name">{{ item.name }}</text>
              </view>
              <!-- 添加光效元素 -->
              <view class="light-effect"></view>
              <view class="shine-effect"></view>
            </template>
            <template v-else>
              <up-button 
                type="warning" 
                size="mini"
                :loading="isDrawing"
                :disabled="isDrawing || drawTimes <= 0"
                @click="startDraw"
                class="draw-btn"
              >
                {{ drawTimes <= 0 ? '次数不足' : '开始抽奖' }}
              </up-button>
              <text class="cost-tips">消耗10积分/次</text>
            </template>
          </view>
        </view>
      </view>
    </view>

    <!-- 中奖记录 -->
    <view class="win-records">
      <view class="section-title">
        <text>中奖记录</text>
        <text class="more" @click="showAllRecords">查看更多</text>
      </view>
      <swiper
        class="records-swiper"
        vertical
        circular
        autoplay
        :interval="3000"
      >
        <swiper-item v-for="(record, index) in winRecords" :key="index">
          <view class="record-item">
            <text class="user">{{ record.user }}</text>
            <text class="prize">抽中了 {{ record.prize }}</text>
            <text class="time">{{ formatTime(record.time) }}</text>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 规则说明 -->
    <view class="rules">
      <view class="section-title">规则说明</view>
      <view class="rule-content">
        <view class="rule-item" v-for="(rule, index) in rules" :key="index">
          {{ index + 1 }}. {{ rule }}
        </view>
      </view>
    </view>

    <!-- 中奖弹窗 -->
    <up-popup v-model="showWinPopup" mode="center" :closeable="false">
      <view class="win-popup">
        <image src="/static/lottery/win.png" class="win-icon" mode="aspectFit" />
        <view class="win-title">恭喜中奖</view>
        <view class="win-prize">{{ currentPrize?.name }}</view>
        <image 
          :src="currentPrize?.image" 
          class="prize-img" 
          mode="aspectFit" 
        />
        <view class="popup-btns">
          <up-button type="primary" @click="closeWinPopup">确定</up-button>
        </view>
      </view>
    </up-popup>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { formatDate } from '@/utils/date'

// 状态
const userPoints = ref(100)
const drawTimes = ref(5)
const isDrawing = ref(false)
const currentIndex = ref(-1)
const showWinPopup = ref(false)
const currentPrize = ref(null)

// 奖品列表
const prizeList = ref([
  { id: 1, name: '10积分', image: '/static/lottery/prize1.png', probability: 30 },
  { id: 2, name: '20积分', image: '/static/lottery/prize2.png', probability: 20 },
  { id: 3, name: '50积分', image: '/static/lottery/prize3.png', probability: 10 },
  { id: 4, name: '优惠券', image: '/static/lottery/prize4.png', probability: 15 },
  { id: 0, name: '抽奖', image: '', probability: 0 }, // 中间按钮
  { id: 5, name: '谢谢参与', image: '/static/lottery/prize5.png', probability: 15 },
  { id: 6, name: '5积分', image: '/static/lottery/prize6.png', probability: 5 },
  { id: 7, name: '实物礼品', image: '/static/lottery/prize7.png', probability: 3 },
  { id: 8, name: '特等奖', image: '/static/lottery/prize8.png', probability: 2 }
])

// 中奖记录
const winRecords = ref([
  { user: '用户1234', prize: '10积分', time: Date.now() - 1000 * 60 * 5 },
  { user: '用户5678', prize: '优惠券', time: Date.now() - 1000 * 60 * 10 },
  { user: '用户9012', prize: '特等奖', time: Date.now() - 1000 * 60 * 15 }
])

// 规则说明
const rules = [
  '每次抽奖消耗10积分',
  '每日可免费获得3次抽奖机会',
  '特等奖每日限量1个',
  '积分奖励将直接发放到账户',
  '实物奖品请在7天内领取'
]

// 开始抽奖
const startDraw = async () => {
  if (isDrawing.value || drawTimes.value <= 0) return
  
  isDrawing.value = true
  userPoints.value -= 10
  drawTimes.value--
  
  const targetIndex = Math.floor(Math.random() * 8)
  const finalIndex = targetIndex >= 4 ? targetIndex + 1 : targetIndex
  
  let speed = 300
  let times = 0
  const minTimes = 30
  const maxTimes = 40
  const totalTimes = minTimes + Math.floor(Math.random() * (maxTimes - minTimes))
  
  const run = () => {
    times++
    currentIndex.value = (currentIndex.value + 1) % 9
    if (currentIndex.value === 4) currentIndex.value = 5
    
    if (times < totalTimes) {
      if (times < 10) {
        speed = Math.max(50, speed - 30)
      } else if (times > totalTimes - 10) {
        speed += Math.pow(times - (totalTimes - 10), 2)
      }
      
      setTimeout(run, speed)
    } else if (currentIndex.value !== finalIndex) {
      setTimeout(run, speed)
    } else {
      // 中奖后的动画效果
      let pulseCount = 0
      const doPulse = () => {
        if (pulseCount < 3) {
          pulseCount++
          setTimeout(doPulse, 500)
        } else {
          isDrawing.value = false
          currentPrize.value = prizeList.value[finalIndex]
          showWinPopup.value = true
          
          winRecords.value.unshift({
            user: '我',
            prize: currentPrize.value.name,
            time: Date.now()
          })
        }
      }
      doPulse()
    }
  }
  
  run()
}

// 格式化时间
const formatTime = (time) => {
  return formatDate(new Date(time), 'MM-dd hh:mm')
}

// 关闭中奖弹窗
const closeWinPopup = () => {
  showWinPopup.value = false
  currentPrize.value = null
  currentIndex.value = -1
}

// 查看所有记录
const showAllRecords = () => {
  uni.navigateTo({
    url: '/pages/lottery/records'
  })
}
</script>

<style lang="scss" scoped>
.lottery-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
  
  .user-info {
    display: flex;
    justify-content: space-around;
    background-color: #fff;
    padding: 30rpx;
    border-radius: 12rpx;
    margin-bottom: 20rpx;
    
    .points,
    .draw-times {
      text-align: center;
      
      .label {
        font-size: 24rpx;
        color: #666;
        margin-bottom: 10rpx;
        display: block;
      }
      
      .num {
        font-size: 36rpx;
        font-weight: bold;
        color: #ff9500;
      }
    }
  }
  
  .lottery-box {
    background-color: #fff;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
    
    .lottery-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 10rpx;
      background: linear-gradient(45deg, #ff9500, #ff5e3a);
      padding: 10rpx;
      border-radius: 12rpx;
      position: relative;
      overflow: hidden;
      
      // 添加抽奖时的整体效果
      &.is-drawing {
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: linear-gradient(45deg, 
            rgba(255, 255, 255, 0.1),
            rgba(255, 255, 255, 0.2)
          );
          animation: gradientMove 2s linear infinite;
        }
      }
      
      .grid-item {
        aspect-ratio: 1;
        position: relative;
        overflow: hidden;
        
        .item-inner {
          width: 100%;
          height: 100%;
          background-color: #fff;
          border-radius: 8rpx;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          position: relative;
          transition: all 0.3s;
        }
        
        &.active {
          .item-inner {
            transform: scale(0.92);
            box-shadow: 0 0 30rpx rgba(255, 149, 0, 0.6);
            animation: activePulse 0.5s ease-in-out;
          }
          
          .light-effect {
            opacity: 1;
            transform: scale(1.2) rotate(180deg);
          }
          
          .shine-effect {
            opacity: 1;
            transform: translateX(100%);
          }
        }
        
        &.is-center {
          .item-inner {
            background: linear-gradient(45deg, #ff9500, #ff5e3a);
            
            .draw-btn {
              animation: buttonPulse 2s ease-in-out infinite;
            }
          }
        }
        
        .prize-content {
          position: relative;
          z-index: 1;
        }
        
        .light-effect {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: radial-gradient(
            circle at center,
            rgba(255, 149, 0, 0.8),
            transparent 70%
          );
          opacity: 0;
          transition: all 0.3s;
          transform: scale(0.8);
        }
        
        .shine-effect {
          position: absolute;
          top: -100%;
          left: -100%;
          width: 200%;
          height: 200%;
          background: linear-gradient(
            to right,
            transparent,
            rgba(255, 255, 255, 0.4),
            transparent
          );
          transform: translateX(-100%);
          transition: all 0.3s;
        }
      }
    }
  }
  
  .win-records {
    background-color: #fff;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
    
    .records-swiper {
      height: 80rpx;
      
      .record-item {
        display: flex;
        align-items: center;
        font-size: 24rpx;
        
        .user {
          color: #666;
          margin-right: 20rpx;
        }
        
        .prize {
          color: #ff9500;
          margin-right: 20rpx;
        }
        
        .time {
          color: #999;
        }
      }
    }
  }
  
  .rules {
    background-color: #fff;
    border-radius: 12rpx;
    padding: 20rpx;
    
    .rule-content {
      font-size: 24rpx;
      color: #666;
      line-height: 1.8;
    }
  }
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .more {
    font-size: 24rpx;
    color: #999;
    font-weight: normal;
  }
}

.win-popup {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  width: 560rpx;
  text-align: center;
  
  .win-icon {
    width: 120rpx;
    height: 120rpx;
    margin-bottom: 20rpx;
  }
  
  .win-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #ff9500;
    margin-bottom: 20rpx;
  }
  
  .win-prize {
    font-size: 32rpx;
    color: #333;
    margin-bottom: 30rpx;
  }
  
  .prize-img {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 30rpx;
  }
  
  .popup-btns {
    display: flex;
    justify-content: center;
  }
}

// 动画关键帧
@keyframes activePulse {
  0% { transform: scale(0.92); }
  50% { transform: scale(0.85); }
  100% { transform: scale(0.92); }
}

@keyframes buttonPulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

@keyframes gradientMove {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

// 中奖弹窗动画优化
.win-popup {
  // ... 其他样式保持不变
  
  animation: popupShow 0.5s ease-out;
  
  .win-icon {
    animation: iconRotate 1s ease-out;
  }
  
  .win-title {
    animation: titleShow 0.5s ease-out 0.3s both;
  }
  
  .win-prize {
    animation: prizeShow 0.5s ease-out 0.6s both;
  }
}

@keyframes popupShow {
  0% { transform: scale(0.5); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes iconRotate {
  0% { transform: scale(0) rotate(-180deg); }
  100% { transform: scale(1) rotate(0); }
}

@keyframes titleShow {
  0% { transform: translateY(-20px); opacity: 0; }
  100% { transform: translateY(0); opacity: 1; }
}

@keyframes prizeShow {
  0% { transform: translateY(20px); opacity: 0; }
  100% { transform: translateY(0); opacity: 1; }
}
</style> 