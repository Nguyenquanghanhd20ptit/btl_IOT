using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
public class Home : PopUp
{
    public static Home instance;
    public GameObject Login;
    public TMP_Text UserName;
    public TMP_Text id;
    private void Awake()
    {
        instance = this;
    }
    public override void Show()
    {
        base.Show();
        UserName.text = GameController.instance.user.username;
        id.text ="ID : "+ GameController.instance.user.meter_serial_number;
    }
    public void OpenCam()
    {
        CamView.instance.Show();
        Hide();
    }
    public void LogOut()
    {
        Login.SetActive(true);
        Hide();
    }
}
