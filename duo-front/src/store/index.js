import { createStore } from 'vuex'

export default createStore({
  state: {
    token: uni.getStorageSync('token') || '',
    userInfo: uni.getStorageSync('userInfo') || null
  },
  
  getters: {
    isLoggedIn: state => !!state.token
  },
  
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      uni.setStorageSync('token', token)
    },
    
    SET_USER_INFO(state, info) {
      state.userInfo = info
      uni.setStorageSync('userInfo', info)
    },
    
    CLEAR_USER(state) {
      state.token = ''
      state.userInfo = null
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
    }
  },
  
  actions: {
    login({ commit }, loginInfo) {
      commit('SET_TOKEN', loginInfo.token)
      commit('SET_USER_INFO', loginInfo.userInfo)
    },
    
    logout({ commit }) {
      commit('CLEAR_USER')
      uni.reLaunch({
        url: '/pages/login/login'
      })
    }
  }
}) 