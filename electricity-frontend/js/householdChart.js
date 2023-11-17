let url = new URL(window.location.href);
var householdId = url.searchParams.get("id");

var oneYearAgo = new Date();
oneYearAgo.setFullYear(oneYearAgo.getFullYear() - 1);
var checkOptionTime = false;

const startDatePicker = flatpickr("#startDatePicker", {
  dateFormat: "Y-m-d H:i",
  onClose: function (selectedDates, dateStr, instance) {
    document.getElementById("startTime").value = dateStr;
    endDatePicker.set("disable", [
      { from: new Date(0), to: new Date(dateStr) },
    ]);
  },
});

const endDatePicker = flatpickr("#endDatePicker", {
  dateFormat: "Y-m-d H:i",
  onClose: function (selectedDates, dateStr, instance) {
    document.getElementById("endTime").value = dateStr;
    startDatePicker.set("disable", [
      { from: new Date(dateStr), to: new Date(9999, 11, 31) },
    ]);
  },
});

document
  .getElementById("timeForm")
  .addEventListener("submit", function (event) {
    event.preventDefault();
    checkOptionTime = true;
    const startTime = document.getElementById("startTime").value;
    const endTime = document.getElementById("endTime").value;

    // Kiểm tra xem ngày kết thúc có hợp lệ không
    const startDate = new Date(startTime);
    const endDate = new Date(endTime);
    if (startDate >= endDate) {
      alert("Ngày kết thúc phải lớn hơn ngày bắt đầu.");
      return;
    }

    // Gửi thời gian đến API bằng cách sử dụng fetch hoặc XMLHttpRequest
    // Ví dụ sử dụng fetch:
    fetch(hostConstant + `/api/v1/household/chart/${householdId}`, {
      method: "POST",
      body: JSON.stringify({
        filters: [
          {
            name: "electricityMonth",
            value: startDate.getTime(),
            operation: "gt",
          },
          {
            name: "electricityMonth",
            value: endDate.getTime(),
            operation: "lt",
          },
        ],
        pageable: {
          page: 1,
          page_size: 500,
          sort: [
            {
              property: "electricityMonth",
              direction: "asc",
            },
          ],
        },
      }),
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((res) => {
        if (res.ok) {
          return res.json();
        } else {
          return res.text();
        }
      })
      .then((data) => {
        console.log("API trả về:", data);
        if (typeof data === "string") {
        } else {
          drawChart(data);
        }
      })
      .catch((error) => {
        console.error("Lỗi:", error);
      });
  });

if (!checkOptionTime) {
  // Tự động gọi API khi trang được tải
  const defaultStartTime = oneYearAgo;
  const defaultEndTime = new Date();
  fetch(hostConstant + `/api/v1/household/chart/${householdId}`, {
    method: "POST",
    body: JSON.stringify({
      filters: [
        {
          name: "electricityMonth",
          value: defaultStartTime.getTime(),
          operation: "gt",
        },
        {
          name: "electricityMonth",
          value: defaultEndTime.getTime(),
          operation: "lt",
        },
      ],
      pageable: {
        page: 1,
        page_size: 500,
        sort: [
          {
            property: "electricityMonth",
            direction: "asc",
          },
        ],
      },
    }),
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => response.json())
    .then((data) => {
      console.log("API trả về khi trang được tải:", data);

      if (typeof data === "string") {
      } else {
        drawChart(data);
      }
    })
    .catch((error) => {
      console.error("Lỗi khi gọi API khi trang được tải:", error);
    });
}
var myChart;
function drawChart(apiData) {
  var chartLabels = [];
  var chartDataValues = [];

  var consumptions = apiData.consumptions;
  var household = apiData.household;

  const householdImfo = document.getElementById("household-imformation");
  householdImfo.innerHTML = "";

  const householdRow = document.createElement("div");

  householdRow.innerHTML = `
    <h2>Thông tin hộ gia đình</h2>
    <p><strong>Công tơ số:</strong> <span id="meterSerialNumber">${household.meter_serial_number}</span></p>
    <p><strong>Tên hộ gia đình:</strong> <span id="householdName">${household.household_name}</span></p>
    <p><strong>Số điện thoại:</strong><span id="phoneNumber">${household.phone_number}</span></p>
    <p><strong>Địa chỉ:</strong><span id="address"> ${household.address}</span></p>
`;
  householdImfo.appendChild(householdRow);

  consumptions.forEach(function (item) {
    var date = parseDateString(item.electricity_month);
    var month = date.getMonth() + 1;
    var year = date.getFullYear();
    console.log(date);
    chartLabels.push("Tháng " + month + "/" + year);
    chartDataValues.push(item.total_consumption);
  });
  console.log(chartLabels);

  var chartData = {
    labels: chartLabels,
    datasets: [
      {
        label: "Số điện",
        data: chartDataValues,
        backgroundColor: "rgba(54, 162, 235, 0.2)",
        borderColor: "rgba(54, 162, 235, 1)",
        borderWidth: 1,
      },
    ],
  };

  var chartOptions = {
    scales: {
      y: {
        beginAtZero: true,
      },
    },
    plugins: {
      tooltip: {
        callbacks: {
          label: function (context) {
            var label = context.dataset.label || "";

            if (label) {
              label += ": ";
            }

            label += context.parsed.y.toFixed(2) + " kWh\n"; // Định dạng hiển thị số kWh
            label += " Số tiền: " + context.parsed.y * 4000 + " VND"; // Định dạng hiển thị số tiền

            return label;
          },
        },
      },
    },
  };

  if (myChart && myChart.destroy) {
    myChart.destroy();
  }

  var ctx = document.getElementById("myChart").getContext("2d");

  myChart = new Chart(ctx, {
    type: "bar",
    data: chartData,
    options: chartOptions,
  });

  if (chartLabels.length > 0)
    document.getElementById("chart-name").innerHTML =
      "Biểu đồ tiêu thụ điện từ " +
      chartLabels[0] +
      " đến " +
      chartLabels[chartLabels.length - 1];
  else document.getElementById("chart-name").innerHTML = "Không có dữ liệu";

  var totalConsumption = 0;

  chartDataValues.forEach((item) => {
    totalConsumption += item;
  });
  document.getElementById("totalConsumption").innerHTML =
    totalConsumption + " kWh";
  console.log(totalConsumption);
}

function parseDateString(dateString) {
  var parts = dateString.match(
    /(\d{2})-(\d{2})-(\d{4}) (\d{2}):(\d{2}):(\d{2})\.(\d{3})/
  );

  if (!parts) {
    throw new Error("Invalid date string format");
  }

  // Tháng trong đối tượng Date bắt đầu từ 0, nên giảm đi 1
  var month = parseInt(parts[2], 10) - 1;

  return new Date(
    parts[3],
    month,
    parts[1],
    parts[4],
    parts[5],
    parts[6],
    parts[7]
  );
}
