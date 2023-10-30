
from fastapi import FastAPI, UploadFile, File
from datetime import datetime
import base64
# import pytesseract
import mysql.connector
from datetime import datetime
from PIL import Image
import numpy as np
import cv2

# init ocr
from paddleocr import PaddleOCR,draw_ocr
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


app = FastAPI()

# Kết nối tới MySQL
db = mysql.connector.connect(
    host="localhost",
    user="root",
    password="keokasu0",
    database="invoid"
)

# Tạo bảng invoice nếu chưa tồn tại
cursor = db.cursor()
cursor.execute("""
    CREATE TABLE IF NOT EXISTS invoice (
        id INT AUTO_INCREMENT PRIMARY KEY,
        id_user VARCHAR(255),
        date DATE,
        total_price FLOAT,
        image MEDIUMTEXT
    )
""")

# API endpoint để xử lý việc upload ảnh hoá đơn
@app.post("/upload_invoice/")
async def upload_invoice( file: UploadFile = File(...)):
    # Đọc file ảnh
    contents = await file.read()
    
    image_cv2 = image_to_cv2(contents)
    # Xử lý ảnh bằng OCR
    result = ocr.ocr(image_cv2, cls=True)
    current_date = string_to_date(result[0][0][1][0])
    id_user = result[0][1][1][0]
    id_user = id_user[3:]
    total_price = int(result[0][2][1][0])
    print("current_date",current_date)
    
    # Chuyển đổi ảnh sang base64
    encoded_image = base64.b64encode(contents).decode('utf-8')
    
    # Lưu thông tin vào database
    current_date = datetime.now().date()
    insert_query = "INSERT INTO invoice (id_user, date, total_price, image) VALUES (%s, %s, %s, %s)"
    cursor.execute(insert_query, (id_user, current_date, total_price, encoded_image))
    db.commit()
    
    return {"id_user": id_user, "date": current_date, "total_price": total_price, "image": encoded_image}
