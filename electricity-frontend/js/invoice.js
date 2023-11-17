let url = new URL(window.location.href);
var householdId = url.searchParams.get('id');

function getInvoice(){
    fetch(hostConstant + `/api/v1/consumption/bill/${householdId}`, {method: 'GET'})
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
        }
        else{
            displayData(data);
        }
    })
    .catch(error => {
        console.error('Lỗi khi gọi API khi trang được tải:', error);
    });
}

function displayData(data){
    const household = data.household;
    const date = parseDateString( data.electricity_month);
    const householdBody = document.getElementById("customer-info");
    const invoiceDetailsBody = document.getElementById('invoice-body');
    const dateBody = document.getElementById("date");
    const totalMoneyBody = document.getElementById("total-money");
    const totalTax = data.monthly_cost * 0.05;
    const totalMoney = data.monthly_cost + totalTax;
    var valueDate = (date.getMonth() + 1) + '/' + date.getFullYear();


    householdBody.innerHTML = '';
    householdBody.innerHTML = ` 
    <h2>Thông tin khách hàng</h2>
    <p><strong>Công tơ số:</strong> ${data.meter_serial_number}</p>
    <p id = "name-customer"><strong>Tên:</strong>${household.household_name}</p>
    <p><strong>Địa chỉ:</strong> ${household.address}</p>
    <p><strong>Số điện thoại:</strong> ${household.phone_number}</p>
    <p><strong>Chỉ số điện hiện tại:</strong> ${data.current_reading}</p>`;

    dateBody.innerHTML = `<p>Tháng:<span>${valueDate}</span></p>`;

    const invoiceDetailsRow = document.createElement("tr");
    invoiceDetailsRow.innerHTML = `
    <td>1</td>
    <td>lưới điện quốc gia</td>
    <td>${data.total_consumption}</td>
    <td>${data.electricity_rate}</td>
    <td>${data.monthly_cost}</td>
    <td>${totalTax}</td>
     `;
     invoiceDetailsBody.appendChild(invoiceDetailsRow);

     totalMoneyBody.innerHTML = `<p><strong>Tổng cộng: </strong>${totalMoney} đ</p>`;

}

document.addEventListener('DOMContentLoaded', function () {
    const currentDate = new Date();
    const formattedDate = currentDate.toLocaleDateString('en-US', { day: 'numeric', month: 'numeric', year: 'numeric' });

    document.getElementById('date').innerText = formattedDate;
});


getInvoice();
function downloadInvoice() {
    const date = document.getElementById("date").textContent;
    const name = document.getElementById("name-customer").textContent;
    const content = document.getElementById('invoice');
    html2pdf(content, {
        margin: 10,
        filename: `Hóa đơn tiền điện ${name} ${date}.pdf`,
        jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' },
    }).then(() => {
        // Conversion to PDF completed, you can further convert the PDF to Word if needed
        console.log('Conversion to PDF completed.');
    });
}


function parseDateString(dateString) {
    var parts = dateString.match(/(\d{2})-(\d{2})-(\d{4}) (\d{2}):(\d{2}):(\d{2})\.(\d{3})/);
    
    if (!parts) {
        throw new Error("Invalid date string format");
    }

    // Tháng trong đối tượng Date bắt đầu từ 0, nên giảm đi 1
    var month = parseInt(parts[2], 10) - 1;

    return new Date(parts[3], month, parts[1], parts[4], parts[5], parts[6], parts[7]);
}
