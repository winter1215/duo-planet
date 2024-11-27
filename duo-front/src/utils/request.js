import axios from "axios";

// 创建 axios 实例
const service = axios.create({
  baseURL: "http://127.0.0.1:8080", // 设置你的API基础URL
  timeout: 15000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 从本地存储获取token
    const token = uni.getStorageSync("token");
    if (token) {
      config.headers["token"] = `${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    // 这里可以根据后端的响应结构进行相应的处理
    if (res.code === 0) {
      return res.data;
    } else {
      // 使用 uni.showToast 替代 uview-plus 的 showToast
      uni.showToast({
        title: res.message || "请求失败",
        icon: "none",
      });
      return Promise.reject(res);
    }
  },
  (error) => {
    uni.showToast({
      title: error.message || "网络错误",
      icon: "none",
    });
    return Promise.reject(error);
  }
);

export default service;
