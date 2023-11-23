from fastapi import FastAPI, UploadFile, File, Request
import base64
from datetime import datetime
import cv2
import numpy as np
import requests
from paddleocr import PaddleOCR

ocr = PaddleOCR(use_angle_cls=True, lang='en')

def image_to_cv2(image_data):
    nparr = np.frombuffer(image_data, np.uint8)
    img_cv2 = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    return img_cv2

def string_to_date(date_string):
    date_object = datetime.strptime(date_string, '%d-%m-%Y').date()
    return date_object

def call_post_api(data, api_url):
    headers = {'Content-Type': 'application/json'}
    response = requests.post(api_url, json=data, headers=headers)
    print(response)
    print(data)
    if response.status_code == 200:
        print("Gửi dữ liệu thành công!")
    else:
        print("Gửi dữ liệu không thành công. Mã lỗi:", response.status_code)

app = FastAPI()

@app.post("/upload_invoice/")
async def upload_invoice(file: UploadFile = File(...), request: Request = None):
    contents = await file.read()
    image_cv2 = image_to_cv2(contents)
    result = ocr.ocr(image_cv2, cls=True)
    current_datetime = datetime.now()
    id_user = request.query_params.get("meter_serial_number", None)
    total_price = int(result[0][2][1][0])
    total_price = int(total_price / 10) + total_price % 10 / 10
    print(total_price)
    print(id_user)
    encoded_image = base64.b64encode(contents).decode('utf-8')
    api_url = 'https://btliot-production.up.railway.app/api/v1/consumption'
    data = {
        "meter_serial_number": id_user,
        "current_reading": total_price,
        "electricity_rate": 4000,
        "electricity_month": int(current_datetime.timestamp()),
    }
    call_post_api(data, api_url)
  
    return {"total_price": total_price, "image": encoded_image}
