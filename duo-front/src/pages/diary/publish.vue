// src/api/auth.js
function login(username, password) {
  return fetch('/api/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
    headers: {
      'Content-Type': 'application/json',
    },
  })
  .then(response => response.json())
  .then(data => {
    if (data.code === 0) {
      localStorage.setItem('token', data.data.token); // 存储 token
      // 跳转到首页
      window.location.href = '/pages/HomePage'; // 根据你的路由配置调整
    } else {
      throw new Error(data.message);
    }
  });
}
