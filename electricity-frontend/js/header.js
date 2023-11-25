
function logout() {
    localStorage.removeItem('userLogger');
    alert("Bạn đã đăng xuất thành công!");
    // Thêm các bước đăng xuất cần thiết, ví dụ: chuyển hướng trang đăng nhập
    window.location.href = "/html/login.html";
}
