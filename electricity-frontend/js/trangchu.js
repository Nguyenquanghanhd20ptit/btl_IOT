var  oneYearAgo = new Date();
oneYearAgo.setFullYear(oneYearAgo.getFullYear() - 1);
var checkOptionTime = false;


document.getElementById("timeForm").addEventListener("submit", function (event) {
    event.preventDefault();
    checkOptionTime = true;
    const startTime = document.getElementById("startTime").value;
    const endTime = document.getElementById("endTime").value;

    // Gửi thời gian đến API bằng cách sử dụng fetch hoặc XMLHttpRequest
    // Ví dụ sử dụng fetch:
    fetch(hostConstant + "/api/v1/household/chart", {
        method: 'POST',
        body: JSON.stringify({
            "filters": [{ "name": "electricityMonth", "value": startDate.getTime(), "operation": "gt" },
            { "name": "electricityMonth", "value": endDate.getTime(), "operation": "lt" }],
            "pageable": {
                "page": 1,
                "page_size": 500,
                "sort": [
                    {
                        "property": "electricityMonth",
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
        .then(data => {
            console.log('API trả về:', data);
            if (typeof data === "string") {
            }
            else{
                drawChart(data);
            }
        })
        .catch(error => {
            console.error('Lỗi:', error);
        });
});

if (!checkOptionTime) {
    // Tự động gọi API khi trang được tải
    const defaultStartTime = oneYearAgo;
    const defaultEndTime = new Date();
    fetch(hostConstant + "/api/v1/household/chart", {
        method: 'POST',
        body: JSON.stringify({
            "filters": [{ "name": "electricityMonth", "value": defaultStartTime.getTime(), "operation": "gt" },
            { "name": "electricityMonth", "value": defaultEndTime.getTime(), "operation": "lt" }],
            "pageable": {
                "page": 1,
                "page_size": 500,
                "sort": [
                    {
                        "property": "electricityMonth",
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
        .then(data => {
            console.log('API trả về khi trang được tải:', data);

            if (typeof data === "string") {
            }
            else{
                drawChart(data);
            }
        })
        .catch(error => {
            console.error('Lỗi khi gọi API khi trang được tải:', error);
        });
}
var myChart;
function drawChart(apiData) {

    var chartLabels = [];
    var chartDataValues = [];

    apiData.forEach(function (item) {
        var date = new Date(item.electricity_month);
        var month = date.getDate();  // đang để thời gian ngược 1/2/2023 nó sẽ hiểu 01 là tháng vì kiểu date nó đặt tháng ở đầu
        var year = date.getFullYear(); 
        console.log(date);
        chartLabels.push('Tháng ' + (month)+"/" + year); 
        chartDataValues.push(item.total_consumption); 
    });
    console.log(chartLabels);

    var chartData = {
        labels: chartLabels,
        datasets: [{
            label: 'Số điện',
            data: chartDataValues,
            backgroundColor: 'rgba(54, 162, 235, 0.2)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
        }]
    };

    var chartOptions = {
        scales: {
            y: {
                beginAtZero: true
            }
        },
        plugins: {
            tooltip: {
                callbacks: {
                    label: function(context) {
                        var label = context.dataset.label || '';
    
                        if (label) {
                            label += ': ';
                        }
    
                        label += context.parsed.y.toFixed(2) + ' kWh\n'; // Định dạng hiển thị số kWh
                        label +=  ' Số tiền: '  + context.parsed.y * 4000 + ' VND'; // Định dạng hiển thị số tiền
    
                        return label;
                    }
                }
            }
        }
    };


    if (myChart && myChart.destroy) {
        myChart.destroy(); 
    }

    var ctx = document.getElementById('myChart').getContext('2d');

    myChart = new Chart(ctx, {
        type: 'bar',
        data: chartData,
        options: chartOptions
    });

    if(chartLabels.length > 0) document.getElementById("chart-name").innerHTML = "Biểu đồ tiêu thụ điện từ " + chartLabels[0] + " đến " + chartLabels[chartLabels.length - 1];
    else document.getElementById("chart-name").innerHTML = "Không có dữ liệu";

    var  totalConsumption = 0

    chartDataValues.forEach(item =>{
        totalConsumption += item;
    });
    document.getElementById('totalConsumption').innerHTML = totalConsumption + " kWh";
    console.log(totalConsumption)
}
