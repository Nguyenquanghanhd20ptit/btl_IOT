
const isAuthenticated  =localStorage.getItem('userLogger');

if (!isAuthenticated && window.location.pathname !== '/html/login.html') {
    window.location.href = '/html/login.html';
}
