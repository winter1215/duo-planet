const baseURL = "http://127.0.0.1:8080/api";

const request = {
  request(options = {}) {
    // 拼接完整请求地址
    options.url = baseURL + options.url;

    // 获取本地存储的token
    const token = uni.getStorageSync("token");
    if (token) {
      options.header = {
        ...options.header,
        token: token,
      };
    }

    // 默认header
    options.header = {
      "Content-Type": "application/json",
      ...options.header,
    };

    return new Promise((resolve, reject) => {
      uni.request({
        ...options,
        success: (res) => {
          if (res.data.code === 0) {
            resolve(res.data.data);
          } else {
            uni.showToast({
              title: res.data.message || "请求失败",
              icon: "none",
            });
            reject(res.data);
          }
        },
        fail: (err) => {
          uni.showToast({
            title: "网络错误",
            icon: "none",
          });
          reject(err);
        },
      });
    });
  },

  get(url, data = {}) {
    return this.request({
      url,
      method: "GET",
      data,
    });
  },

  post(url, data = {}) {
    return this.request({
      url,
      method: "POST",
      data,
    });
  },
};

export default request;
