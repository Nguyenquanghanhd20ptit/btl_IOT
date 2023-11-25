let nextUserId = 4;
const PAGE_SIZE = 6;
let currentPage = 1;


$(document).ready(function () {
    let timeoutId;
    $('#searchInput').on('input', function () {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(function () {
            searchHouseholds(1);
        }, 500); 
    });
});


function searchHouseholds(page) {
    let searchContent = document.getElementById('searchInput').value;
    fetch( hostConstant +`/api/v1/household/search`, {
        method: 'POST',
        body: JSON.stringify({
            "keyword": searchContent,
            "pageable": {
                "page": page,
                "page_size": PAGE_SIZE,
                "sort": [
                    {
                        "property": "id",
                        "direction": "asc"
                    }
                ]
            }
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => displayData(data))
        .catch(error => console.error('Error fetching orders:', error));
}


function displayData(data) {
    const households = data.items;
    const total = data.total;
    const totalBody = document.getElementById('total');
    const householdsBody = document.getElementById('households-body');
    const pagination = document.getElementById('pagination');
    householdsBody.innerHTML = '';
    pagination.innerHTML = '';
    totalBody.innerHTML = '';

    const totalRow = document.createElement('p');
    totalRow.textContent = 'Tổng số hộ gia đình: ' + total;
    totalBody.appendChild(totalRow);

    households.forEach(household => {
        const householdRow = document.createElement('tr');
        householdRow.innerHTML = `
      <td>${household.meter_serial_number}</td>
      <td>${household.household_name}</td>
      <td>${household.phone_number}</td>
      <td>${household.address}</td>
      <td>${household.created_at}</td>
      <td><div class="action-menu" onclick="toggleMenu(${household.id})">
      <button class="action-button" data-household-id="${household.id}">
          <i class="fas fa-cogs"></i>
      </button>
    <div class="menu-content" id=menu-content${household.id}>
      <button class="menu-content-button" onclick="edit(${household.id})"><i class="fas fa-pencil-alt"></i> Sửa</button>
      <button class="menu-content-button" onclick="deleteItem(${household.id})"><i class="fas fa-trash"></i> Xóa</button>
      <button class="menu-content-button" onclick="exportInvoice(${household.id})"><i class="fas fa-file-export"></i> Xuất hóa đơn</button>
      <button class="menu-content-button" onclick="viewChart(${household.id})"><i class="fas fa-chart-bar"></i> Xem biểu đồ</button>
  </div>
  </div>
  </td>`;
      householdsBody.appendChild(householdRow);
    });

    const totalPages = Math.ceil(total / PAGE_SIZE);
    for (let i = 1; i <= totalPages; i++) {
        const pageLink = document.createElement('a');
        pageLink.href = 'javascript:void(0);';
        pageLink.textContent = i;
        if (currentPage == i) {
            pageLink.classList.add('active');
        }
        pageLink.onclick = () => {
            currentPage = i;
            searchHouseholds(i);
        }
        pagination.appendChild(pageLink);
    }
}

searchHouseholds(1);



function openModal() {
  document.getElementById("myModal").style.display = "block";
}

function closeModal() {
  document.getElementById("myModal").style.display = "none";
}

function addNewUser() {
  const name = document.getElementById("newUserName").value;
  const phone = document.getElementById("newUserPhone").value;
  const email = document.getElementById("newUserEmail").value;
  const consumption = document.getElementById("newUserConsumption").value;

  if (name && phone && email && consumption) {
    const newUser = {
      id: nextUserId,
      name: name,
      phone: phone,
      email: email,
      consumption: parseInt(consumption),
    };
    userData.push(newUser);
    nextUserId++;

    updateChart();
    closeModal();
  } else {
    alert("Vui lòng điền đầy đủ thông tin người dùng.");
  }
}

document.addEventListener('mousedown', function (event) {
  const button = event.target.closest('.action-button');
  const menuContent = event.target.closest('.menu-content');
  const menuContentButton = event.target.closest('.menu-content-button');
  
  if (button && menuContent) {
      toggleMenu(button.getAttribute('data-household-id'));
  } else if(menuContentButton){
    console.log("chuyen trang");
  }
  else {
      closeAllMenus();
  }
});

function closeAllMenus() {
    const allMenus = document.querySelectorAll('.menu-content');
    allMenus.forEach(menu => {
        menu.style.display = 'none';
    });
}

function toggleMenu(householdId) {
    console.log("fđfdffd"+householdId);
    const menu = document.querySelector(`#menu-content${householdId}`);
    menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
}

function openEditModal() {
  document.getElementById("editModal").style.display = "block";
}

function closeEditModal() {
  document.getElementById("editModal").style.display = "none";
}

function edit(householdId) {
  // Kiểm tra xem householdId có tồn tại hay không
  if (!householdId) {
      console.error('Invalid householdId:', householdId);
      return;
  }

  // Gọi hàm để lấy thông tin hộ gia đình   
  getHouseholdInfo(householdId);
}

function getHouseholdInfo(householdId) {
  // Gọi API để lấy thông tin hộ gia đình
  fetch(hostConstant + `/api/v1/household/${householdId}`)
      .then(response => {
          if (!response.ok) {
              throw new Error(`HTTP error! Status: ${response.status}`);
          }
          return response.json();
      })
      .then(existingHousehold => {
          // Kiểm tra xem dữ liệu đã được trả về hay không
          if (existingHousehold) {
              // Hiển thị thông tin lên modal
              document.getElementById("editUserName").value = existingHousehold.household_name;
              document.getElementById("editUserPhone").value = existingHousehold.phone_number;
              document.getElementById("editUserAddress").value = existingHousehold.address;

              // Cập nhật tiêu đề và nút của modal
              const editModalContent = document.getElementById("editModal").getElementsByClassName("modal-content")[0];
              editModalContent.getElementsByTagName("h4")[0].textContent = "Chỉnh sửa thông tin hộ gia đình";

              const updateButton = editModalContent.getElementsByTagName("button")[0];
              updateButton.textContent = "Cập nhật";
              updateButton.onclick = function () {


                  performUpdate(householdId);
              };

              openEditModal();
          } else {
              console.error('Existing Household Data is undefined or null.');
          }
      })
      .catch(error => {
          console.error('Error fetching household data:', error.message);

      });
}
// cập nhật thông tin hộ gia đình
function performUpdate(householdId) {
  const newName = document.getElementById("editUserName").value;
  const newPhone = document.getElementById("editUserPhone").value;
  const newAddress = document.getElementById("editUserAddress").value;

  if (newName && newPhone && newAddress) {
      const updatedHousehold = {
          id: householdId,
          household_name: newName,
          phone_number: newPhone,
          address: newAddress,
      };

      fetch(hostConstant + `/api/v1/household/update/${householdId}`, {
          method: 'PUT',
          body: JSON.stringify(updatedHousehold),
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

        if (typeof data === "string") {
            document.getElementById("update-household-error").textContent = data;
        }
        else{
            console.log('Household updated successfully', data);

            searchHouseholds(currentPage);

            closeEditModal();
        }
        })
        .catch(error => {
            console.error('Error updating household:', error.message);
            // Xử lý lỗi và thông báo cho người dùng nếu cần
        });
  } else {
      alert("Vui lòng điền đầy đủ thông tin hộ gia đình.");
  }
}

function deleteItem(householdId) {
  if (confirm("Bạn có chắc chắn muốn xóa hộ gia đình này không?")) {
      fetch(`${hostConstant}/api/v1/household/${householdId}`, {
          method: 'DELETE',
          headers: {
              'Content-Type': 'application/json'
          }
      })
          .then(response => response.json())
          .then(data => {
              console.log('Household deleted successfully', data);
              // Cập nhật danh sách sau khi xóa
              searchHouseholds(currentPage);
          })
          .catch(error => console.error('Error deleting household:', error));
  }
}

  function exportInvoice(householdId) {
    var newURL = "http://" + window.location.host + `/html/invoice.html?id=${householdId}`;
    window.location.href = newURL;
  }
  
  function viewChart(householdId) {
    console.log(householdId);
    var newURL = "http://" + window.location.host + `/html/householdChart.html?id=${householdId}`;
    window.location.href = newURL;
  }


  //Add user

  function addNewHousehold() {
    const householdName = document.getElementById("newHouseholdName").value;
    const householdPhone = document.getElementById("newHouseholdPhone").value;
    const householdAddress = document.getElementById("newHouseholdAddress").value;
    const householdUsername = document.getElementById("newHouseholdUsername").value;
    const householdPassword = document.getElementById("newHouseholdPassword").value;
    const householdConfirmPassword = document.getElementById("newHouseholdConfirmPassword").value;

    if (householdName && householdPhone && householdAddress) {
        const requestBody = {
            "household_name": householdName,
            "address": householdAddress,
            "phone_number": householdPhone,
            "username":householdUsername,
            "password": householdPassword,
            "confirm_password" : householdConfirmPassword
        };

        fetch(hostConstant + '/api/v1/household', {
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
        .then(newHousehold => {
            if (typeof newHousehold === "string") {
                document.getElementById("add-household-error").textContent = newHousehold;
            }
            else{
                updateHouseholdsList(newHousehold);
                closeModal();
            }
        })
        .catch(error => console.error('Error adding new household:', error));
    } else {
        alert("Vui lòng điền đầy đủ thông tin hộ gia đình.");
    }
}

function updateHouseholdsList(newHousehold) {
  const householdsBody = document.getElementById('households-body');
  
  // Tạo một dòng mới trong bảng
  const householdRow = document.createElement('tr');
  householdRow.innerHTML = `
    <td>${newHousehold.meter_serial_number}</td>
    <td>${newHousehold.household_name}</td>
    <td>${newHousehold.phone_number}</td>
    <td>${newHousehold.address}</td>
    <td>${newHousehold.created_at}</td>
    <td>
      <div class="action-menu" onclick="toggleMenu(${newHousehold.id})">
        <button class="action-button" data-household-id="${newHousehold.id}">
          <i class="fas fa-cogs"></i>
        </button>
        <div class="menu-content" id=menu-content${newHousehold.id}>
          <button class="menu-content-button" onclick="edit(${newHousehold.id})"><i class="fas fa-pencil-alt"></i> Sửa</button>
          <button class="menu-content-button" onclick="deleteItem(${newHousehold.id})"><i class="fas fa-trash"></i> Xóa</button>
          <button class="menu-content-button" onclick="exportInvoice(${newHousehold.id})"><i class="fas fa-file-export"></i> Xuất hóa đơn</button>
          <button class="menu-content-button" onclick="viewChart(${newHousehold.id})"><i class="fas fa-chart-bar"></i> Xem biểu đồ</button>
        </div>
      </div>
    </td>`;

  // Thêm dòng mới vào đầu danh sách
  householdsBody.insertBefore(householdRow, householdsBody.firstChild);
  
  // Cập nhật tổng số hộ gia đình
  const total = document.getElementById('total');
  total.textContent = 'Tổng số hộ gia đình: ' + (parseInt(total.textContent.split(':')[1]) + 1);
}
