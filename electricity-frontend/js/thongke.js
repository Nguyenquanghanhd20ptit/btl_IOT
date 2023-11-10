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
function edit(householdId) {
  }
  
  function deleteItem(householdId) {
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