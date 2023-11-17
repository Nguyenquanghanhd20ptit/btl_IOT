
function performLogin(){
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    
    const requestBody = {
        "username": username,
        "password": password,
    };
    fetch("http://localhost:8080/api/v1/authentication/admin/login", {
        method: 'POST',
        body: JSON.stringify(requestBody),
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then((res) => {
        if (res.ok) {
            return res.json();
        } else {
            return res.text(); 
        }
    })
        .then(data => {
            console.log('API trả về khi trang được tải:', data);
            if (typeof data === "string") {
                document.getElementById("login-error").textContent = data;
            }
            else{
                localStorage.setItem('userLogger', data);
                window.location.href = '/html/chart.html';
            }
        })
        .catch(error => {
            console.error('Lỗi khi gọi API khi trang được tải:', error);
        });
}