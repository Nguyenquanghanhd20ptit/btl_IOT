using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CameraAndroid : MonoBehaviour
{
    public RawImage rawImage; // UI RawImage to display the camera feed
    private WebCamTexture webCamTexture; 
    private void OnEnable()
    {
        OpenCam();
    }
    private void OnDisable()
    {
        webCamTexture.Stop();
    }
    void Start()
    { 
        // Get the available camera devices
        WebCamDevice[] devices = WebCamTexture.devices;
#if PLATFORM_ANDROID
        for (int i = 0; i < devices.Length; i++)
        {
            if (!devices[i].isFrontFacing)
            {
                webCamTexture = new WebCamTexture(devices[i].name, Screen.width, Screen.height);
                rawImage.texture = webCamTexture;
                 
                webCamTexture.Play();
                break;
            }
        }
#endif
#if UNITY_EDITOR
        if (devices.Length > 0)
        {
            // Use the first available camera
            webCamTexture = new WebCamTexture(devices[0].name, Screen.width, Screen.height);
            rawImage.texture = webCamTexture;
            webCamTexture.Play();
        }
        else
        {
            Debug.LogError("No camera device found");
        }

#endif
    }
    public void OpenCam()
    {
        rawImage.texture = webCamTexture;
        if (webCamTexture!= null)    
            webCamTexture.Play(); 
    }
    public void Capture()
    {
        Texture2D photo = new Texture2D(webCamTexture.width, webCamTexture.height); 
        photo.SetPixels(webCamTexture.GetPixels());
        photo.Apply();
        rawImage.texture = photo;
        webCamTexture.Stop();
        CamView.instance. PopUpConfirm.gameObject.SetActive(true);
    }

    private void Update()
    {
        /*int orient = -webCamTexture.videoRotationAngle;
        rawImage.rectTransform.localEulerAngles = new Vector3(0, 0, orient);*/
    }
}
