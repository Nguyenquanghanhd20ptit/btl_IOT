import base64
# import pytesseract
from datetime import datetime

import cv2
import numpy as np
import requests
from fastapi import FastAPI, UploadFile, File
# init ocr
from paddleocr import PaddleOCR

# Paddleocr supports Chinese, English, French, German, Korean and Japanese.
# You can set the parameter `lang` as `ch`, `en`, `fr`, `german`, `korean`, `japan`
# to switch the language model in order.
ocr = PaddleOCR(use_angle_cls=True, lang='en') # need to run only once to download and load model into memory



# Hàm chuyển đổi ảnh thành đối tượng cv2
def image_to_cv2(image_data):
    nparr = np.frombuffer(image_data, np.uint8)
    img_cv2 = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    return img_cv2


def string_to_date(date_string):
    date_object = datetime.strptime(date_string, '%d-%m-%Y').date()
    return date_object

def call_post_api(data,api_url):
    # Đặt tiêu đề 'Content-Type' cho yêu cầu
    headers = {'Content-Type': 'application/json'}

    # Sử dụng phương thức POST để gửi dữ liệu JSON tới API
    response = requests.post(api_url, json=data, headers=headers)
    print(response)
    print(data)

    # Kiểm tra xem yêu cầu có thành công không
    if response.status_code == 200:
        print("Gửi dữ liệu thành công!")
    else:
        print("Gửi dữ liệu không thành công. Mã lỗi:", response.status_code)


app = FastAPI()

# API endpoint để xử lý việc upload ảnh hoá đơn
@app.post("/upload_invoice/")
async def upload_invoice( file: UploadFile = File(...)):
    # Đọc file ảnh
    contents = await file.read()
    
    image_cv2 = image_to_cv2(contents)
    # Xử lý ảnh bằng OCR
    result = ocr.ocr(image_cv2, cls=True)
    current_date = string_to_date(result[0][0][1][0])
    current_datetime = datetime(current_date.year, current_date.month, current_date.day)
    id_user = result[0][1][1][0]
    id_user = id_user[3:]
    total_price = int(result[0][2][1][0])
    print("current_date",current_date)
    
    # Chuyển đổi ảnh sang base64
    encoded_image = base64.b64encode(contents).decode('utf-8')

    api_url = 'https://btliot-production.up.railway.app/api/v1/consumption'

    data = {
        "meter_serial_number": id_user,
        "current_reading": total_price,
        "electricity_rate": 4000,
        "electricity_month": int(current_datetime.timestamp()),
    }

    call_post_api(data,api_url)
    return {"id_user": id_user, "date": current_date, "total_price": total_price}
