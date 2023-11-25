using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEngine;
using UnityEngine.Networking;
using UnityEngine.UI;
using TMPro;
using System;
using System.Net.Http;

public class Bill
{
    public float id;
    public float current_reading;
    public float previous_reading;
    public float monthly_cost;
    public override string ToString()
    {
        return current_reading+" "+ previous_reading;
    }
}
public class User
{
    public User(string username, string password)
    {
        this.username = username;
        this.password = password;
    }
    public string username;
    public string password;
}
public class UserInfor 
{
    public int id;
    public string username;
    public string household_name;
    public string address;
    public string phone_number;
    public string meter_serial_number;
    
    public UserInfor(int id, string username, string household_name, string address, string phone_number, string meter_serial_number)
    {
        this.id = id;
        this.username = username;
        this.household_name = household_name;
        this.address = address;
        this.phone_number = phone_number;
        this.meter_serial_number = meter_serial_number;
    }
    public override string ToString()
    {
        return base.ToString()+ username +" "+ meter_serial_number;

    }
}
public class NewBehaviourScript : MonoBehaviour
{
    public Text t;
    public RawImage image;
    public TMP_InputField input;
    
    
    public TMP_InputField username;
    public TMP_InputField password;


    // Start is called before the first frame update
    void Start()
    {
        password.contentType = TMP_InputField.ContentType.Password;

    }
    public void GetAPI()
    {
        string s = "?meter_serial_number="+GameController.instance.user.meter_serial_number;
       StartCoroutine(Upload("https://j79818td-8000.asse.devtunnels.ms/upload_invoice/"+s));
      // StartCoroutine(Upload("https://vj2d317v-5000.asse.devtunnels.ms/show"));
   

        // StartCoroutine(GetRequest("http://localhost:8080/ManageMarketTonPc/API"));
    }
    void FlipRawImageX(RawImage rawImage)
    {
        // Get the current UVRect of the RawImage
        Rect uvRect = rawImage.uvRect;

        // Flip the X component of the UVRect
        uvRect.x += uvRect.width;
        uvRect.width = -uvRect.width;

        // Apply the new UVRect to the RawImage
        rawImage.uvRect = uvRect;
    }
    IEnumerator GetRequest(string uri)
    {
        using (UnityWebRequest webrequest = UnityWebRequest.Get(uri)){
            yield return webrequest.SendWebRequest();
            switch (webrequest.result)
            {
                case UnityWebRequest.Result.ConnectionError:
                case UnityWebRequest.Result.DataProcessingError:
                    Debug.LogError(string.Format("something went wrong : {0}", webrequest.error));
                    break;
                case UnityWebRequest.Result.Success:
                    Debug.Log(webrequest.downloadHandler.text);
                    t.text = webrequest.downloadHandler.text;
                    break;
            }
        }
    }
    private static Texture2D GetReadableTexture2d(Texture texture)
    {
        var tmp = RenderTexture.GetTemporary(
            texture.width,
            texture.height,
            0,
            RenderTextureFormat.Default,
            RenderTextureReadWrite.Linear
        );
        Graphics.Blit(texture, tmp);

        var previousRenderTexture = RenderTexture.active;
        RenderTexture.active = tmp;

        var texture2d = new Texture2D(texture.width, texture.height);
        texture2d.ReadPixels(new Rect(0, 0, texture.width, texture.height), 0, 0);
        texture2d.Apply();

        RenderTexture.active = previousRenderTexture;
        RenderTexture.ReleaseTemporary(tmp);
        return texture2d;
    }
    public async void Login()
    {
        User user = new User(username.text.Trim(), password.text.Trim());
        var jsonString = JsonUtility.ToJson(user);
        Debug.Log(jsonString);
        var client = new HttpClient();
        var request = new HttpRequestMessage(HttpMethod.Post, "https://btliot-production.up.railway.app/api/v1/authentication/login");
        var content = new StringContent(jsonString, null, "application/json");
        request.Content = content;
        var response = await client.SendAsync(request);
        String jsonRep = await response.Content.ReadAsStringAsync();
        try 
        {
            UserInfor obj = JsonUtility.FromJson<UserInfor>(jsonRep);
            Debug.Log("successfull");
            Debug.Log(obj.ToString());
            GameController.instance.user = obj;
            Home.instance.Show();
            Home.instance.Login.SetActive(false);
        }
        catch (Exception e)
        {
            Debug.Log("Faile"+ e);
            Dialog.instance.ShowDiaLog();
        }

        
      
    }
   
    
    IEnumerator Upload(string path)
    { 
        Texture2D texture1 = GetReadableTexture2d(image.mainTexture); ;
        Texture2D texture = RotateTexture(texture1, 90);

        // Convert the texture to a byte array
        byte[] imageBytes = texture.EncodeToPNG(); 


        // Tạo UnityWebRequest
        WWWForm form = new WWWForm();
        form.AddBinaryData("file", imageBytes, "screenshot.png", "image/png");

        using (UnityWebRequest www = UnityWebRequest.Post(path, form))
        {

            www.uploadHandler.contentType = "multipart/form-data";
            yield return www.SendWebRequest();

            if (www.result != UnityWebRequest.Result.Success)
            {
                Debug.Log("Error uploading: " + www.error);
            }
            else
            {
                Debug.Log("Upload complete!");
                Debug.Log(  www.downloadHandler.text);
                Bill bill = JsonUtility.FromJson<Bill>(www.downloadHandler.text);
                Debug.Log(bill);
                CamView.instance.OpenDialog(bill.monthly_cost,bill.current_reading,bill.previous_reading);
            }
        }
    }
    private Texture2D RotateTexture(Texture2D originalTexture, float angle)
    {
        Texture2D rotatedTexture = new Texture2D(originalTexture.width, originalTexture.height);
        Color32[] originalPixels = originalTexture.GetPixels32();
        Color32[] rotatedPixels = new Color32[originalPixels.Length];

        int w = originalTexture.width;
        int h = originalTexture.height;

        float radianAngle = Mathf.Deg2Rad * angle;
        float cosTheta = Mathf.Cos(radianAngle);
        float sinTheta = Mathf.Sin(radianAngle);

        int centerX = w / 2;
        int centerY = h / 2;

        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                int index = x + y * w;
                int offsetX = x - centerX;
                int offsetY = y - centerY;

                int rotatedX = Mathf.RoundToInt(centerX + offsetX * cosTheta - offsetY * sinTheta);
                int rotatedY = Mathf.RoundToInt(centerY + offsetX * sinTheta + offsetY * cosTheta);

                if (rotatedX >= 0 && rotatedX < w && rotatedY >= 0 && rotatedY < h)
                {
                    int rotatedIndex = rotatedX + rotatedY * w;
                    rotatedPixels[index] = originalPixels[rotatedIndex];
                }
            }
        }

        rotatedTexture.SetPixels32(rotatedPixels);
        rotatedTexture.Apply();
        return rotatedTexture;
    }
    Texture2D GetTextureFromRawImage(UnityEngine.UI.RawImage rawImage)
    {
        Texture rawTexture = image.mainTexture;

        // Ensure that the texture is not null and is of type Texture2D
        if (rawTexture != null && rawTexture is Texture2D)
        {
            Texture2D texture = (Texture2D)rawTexture;

            // Encode the texture into PNG format
            byte[] imageBytes = texture.EncodeToPNG();

            return texture;
        }
        else
        {
            Debug.LogError("Texture is null or not of type Texture2D");
            return null;
        }
        return null;
    }
    byte[] ConvertTextureToBytes(Texture2D texture)
    {

        // Encode the texture to a PNG byte array
        byte[] bytes = texture.EncodeToPNG();

        return bytes;
    }

}
