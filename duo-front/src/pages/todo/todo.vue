<template>
  <view class="todo-container">
    <!-- 顶部统计 -->
    <view class="stats-card">
      <view class="stat-item">
        <text class="num">{{ stats.total }}</text>
        <text class="label">全部</text>
      </view>
      <view class="stat-item">
        <text class="num">{{ stats.pending }}</text>
        <text class="label">待办</text>
      </view>
      <view class="stat-item">
        <text class="num">{{ stats.completed }}</text>
        <text class="label">已完成</text>
      </view>
    </view>

    <!-- 添加待办 -->
    <view class="add-todo">
      <up-input
        v-model="newTodo"
        placeholder="添加待办事项"
        border="surround"
        confirmType="done"
        @confirm="handleAddTodo"
      >
        <template #suffix>
          <up-button 
            type="primary" 
            size="mini" 
            @click="handleAddTodo"
            :disabled="!newTodo.trim()"
          >
            添加
          </up-button>
        </template>
      </up-input>
    </view>

    <!-- 筛选栏 -->
    <view class="filter-bar">
      <up-tabs
        :list="filterTabs"
        v-model="currentTab"
        @change="handleTabChange"
      ></up-tabs>
    </view>

    <!-- 待办列表 -->
    <scroll-view 
      class="todo-list"
      scroll-y
      @scrolltolower="loadMore"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view 
        class="todo-item"
        v-for="item in filteredTodos"
        :key="item.id"
      >
        <view class="todo-content">
          <up-checkbox
            v-model="item.completed"
            @change="(value) => toggleTodo(item.id, value)"
            :disabled="item.loading"
          ></up-checkbox>
          <view 
            :class="['todo-text', { completed: item.completed }]"
            @click="editTodo(item)"
          >
            {{ item.content }}
          </view>
        </view>
        
        <view class="todo-footer">
          <view class="todo-info">
            <up-tag 
              :text="item.priority" 
              :type="getPriorityType(item.priority)"
              size="mini"
            ></up-tag>
            <text class="time">{{ formatDate(item.createTime) }}</text>
          </view>
          
          <view class="todo-actions">
            <up-button
              type="warning"
              size="mini"
              plain
              @click="editTodo(item)"
            >编辑</up-button>
            <up-button
              type="error"
              size="mini"
              plain
              @click="deleteTodo(item.id)"
            >删除</up-button>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <up-empty
        v-if="filteredTodos.length === 0"
        mode="data"
        text="暂无待办事项"
      ></up-empty>
    </scroll-view>

    <!-- 编辑弹窗 -->
    <up-popup v-model="showEditPopup" mode="center">
      <view class="edit-popup">
        <view class="popup-title">编辑待办</view>
        <up-input
          v-model="editForm.content"
          placeholder="请输入待办内容"
          border="surround"
        />
        <view class="priority-select">
          <text class="label">优先级：</text>
          <up-radio-group v-model="editForm.priority">
            <up-radio
              v-for="item in priorityOptions"
              :key="item.value"
              :label="item.label"
              :name="item.value"
            ></up-radio>
          </up-radio-group>
        </view>
        <view class="popup-buttons">
          <up-button @click="showEditPopup = false">取消</up-button>
          <up-button type="primary" @click="handleUpdateTodo">确定</up-button>
        </view>
      </view>
    </up-popup>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { formatDate } from '@/utils/date'

// 状态
const newTodo = ref('')
const currentTab = ref(0)
const refreshing = ref(false)
const showEditPopup = ref(false)
const editForm = ref({
  id: null,
  content: '',
  priority: 'normal'
})

// 假数据
const todoList = ref([
  {
    id: 1,
    content: '完成待办功能开发',
    completed: false,
    priority: 'high',
    createTime: new Date().getTime(),
    loading: false
  },
  {
    id: 2,
    content: '修复已知bug',
    completed: true,
    priority: 'normal',
    createTime: new Date().getTime() - 86400000,
    loading: false
  }
])

// 配置项
const filterTabs = [
  { name: '全部' },
  { name: '待办' },
  { name: '已完成' }
]

const priorityOptions = [
  { label: '普通', value: 'normal' },
  { label: '重要', value: 'high' },
  { label: '紧急', value: 'urgent' }
]

// 计算属性
const stats = computed(() => {
  return {
    total: todoList.value.length,
    pending: todoList.value.filter(item => !item.completed).length,
    completed: todoList.value.filter(item => item.completed).length
  }
})

const filteredTodos = computed(() => {
  switch (currentTab.value) {
    case 1: // 待办
      return todoList.value.filter(item => !item.completed)
    case 2: // 已完成
      return todoList.value.filter(item => item.completed)
    default: // 全部
      return todoList.value
  }
})

// 方法
const handleAddTodo = async () => {
  if (!newTodo.value.trim()) return
  
  try {
    // const res = await request.post('/todo/add', {
    //   content: newTodo.value.trim(),
    //   priority: 'normal'
    // })
    
    // 模拟API响应
    const todo = {
      id: Date.now(),
      content: newTodo.value.trim(),
      completed: false,
      priority: 'normal',
      createTime: new Date().getTime(),
      loading: false
    }
    
    todoList.value.unshift(todo)
    newTodo.value = ''
    
    uni.showToast({
      title: '添加成功',
      icon: 'success'
    })
  } catch (error) {
    uni.showToast({
      title: '添加失败',
      icon: 'none'
    })
  }
}

const toggleTodo = async (id, value) => {
  const todo = todoList.value.find(item => item.id === id)
  if (!todo || todo.loading) return
  
  todo.loading = true
  try {
    // await request.post('/todo/update', {
    //   id,
    //   completed: value
    // })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    todo.completed = value
  } catch (error) {
    uni.showToast({
      title: '操作失败',
      icon: 'none'
    })
  } finally {
    todo.loading = false
  }
}

const editTodo = (todo) => {
  editForm.value = {
    id: todo.id,
    content: todo.content,
    priority: todo.priority
  }
  showEditPopup.value = true
}

const handleUpdateTodo = async () => {
  if (!editForm.value.content.trim()) {
    uni.showToast({
      title: '请输入待办内容',
      icon: 'none'
    })
    return
  }
  
  try {
    // await request.post('/todo/update', editForm.value)
    
    // 模拟API调用
    const todo = todoList.value.find(item => item.id === editForm.value.id)
    if (todo) {
      todo.content = editForm.value.content.trim()
      todo.priority = editForm.value.priority
    }
    
    showEditPopup.value = false
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

const deleteTodo = (id) => {
  uni.showModal({
    title: '提示',
    content: '确定要删除这条待办吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          // await request.post('/todo/delete', { id })
          
          // 模拟API调用
          const index = todoList.value.findIndex(item => item.id === id)
          if (index > -1) {
            todoList.value.splice(index, 1)
          }
          
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
        } catch (error) {
          uni.showToast({
            title: '删除失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

const handleTabChange = (index) => {
  currentTab.value = index
}

const loadMore = async () => {
  // 实现加载更多逻辑
}

const onRefresh = async () => {
  refreshing.value = true
  try {
    // await loadTodoList()
    // 模拟刷新
    await new Promise(resolve => setTimeout(resolve, 1000))
  } finally {
    refreshing.value = false
  }
}

const getPriorityType = (priority) => {
  switch (priority) {
    case 'high':
      return 'warning'
    case 'urgent':
      return 'error'
    default:
      return 'primary'
  }
}

// 生命周期
onMounted(() => {
  // loadTodoList()
})
</script>

<style lang="scss" scoped>
.todo-container {
  height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
  
  .stats-card {
    background-color: #fff;
    padding: 20rpx;
    display: flex;
    justify-content: space-around;
    margin-bottom: 20rpx;
    
    .stat-item {
      text-align: center;
      
      .num {
        font-size: 36rpx;
        font-weight: bold;
        color: #2979ff;
        display: block;
      }
      
      .label {
        font-size: 24rpx;
        color: #666;
      }
    }
  }
  
  .add-todo {
    padding: 20rpx;
    background-color: #fff;
  }
  
  .filter-bar {
    background-color: #fff;
    margin-bottom: 20rpx;
  }
  
  .todo-list {
    flex: 1;
    padding: 0 20rpx;
    
    .todo-item {
      background-color: #fff;
      border-radius: 12rpx;
      padding: 20rpx;
      margin-bottom: 20rpx;
      
      .todo-content {
        display: flex;
        align-items: center;
        margin-bottom: 16rpx;
        
        .todo-text {
          margin-left: 20rpx;
          flex: 1;
          
          &.completed {
            text-decoration: line-through;
            color: #999;
          }
        }
      }
      
      .todo-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .todo-info {
          display: flex;
          align-items: center;
          gap: 16rpx;
          
          .time {
            font-size: 24rpx;
            color: #999;
          }
        }
        
        .todo-actions {
          display: flex;
          gap: 16rpx;
        }
      }
    }
  }
}

.edit-popup {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  width: 600rpx;
  
  .popup-title {
    font-size: 32rpx;
    font-weight: bold;
    text-align: center;
    margin-bottom: 30rpx;
  }
  
  .priority-select {
    margin: 30rpx 0;
    display: flex;
    align-items: center;
    
    .label {
      margin-right: 20rpx;
    }
  }
  
  .popup-buttons {
    display: flex;
    justify-content: space-between;
    margin-top: 40rpx;
    gap: 20rpx;
  }
}

:deep(uni-page-body),
:deep(uni-page-wrapper) {
  height: 100vh !important;
}
</style> 