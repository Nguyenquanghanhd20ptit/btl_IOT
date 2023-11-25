using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
public class CamView : PopUp
{
    public static CamView instance;
    public GameObject PopUpConfirm;
    public GameObject PopUpDialog;
    public CameraAndroid camadr;
    public TMP_Text txt;
    public TMP_Text cur;
    public TMP_Text pre;
    private void Awake()
    {
        instance = this;
    }
    private void OnEnable()
    { 
    }
    public void BackHome()
    {
        Home.instance.Show();
        Hide();
    }
    public void CancelSend()
    {
        camadr.OpenCam();
        PopUpConfirm.SetActive(false);
    }
    public void OpenDialog(float money, float cur, float pre){
        PopUpDialog.SetActive(true);
        PopUpConfirm.SetActive(false);
        this.cur.text = "Số điện: " + cur;
        this.pre.text = "Tháng trước: " + pre;
        StartCoroutine(ShowString(money));
        
    }
    IEnumerator ShowString(float money)
    {
        string s = "Số tiền bạn phải trả tháng này là :"+ money+" vnd"  ;
        int amount = s.Length;
        int cnt = 0;
        while (cnt <amount+1)
        {
            txt.text = s.Substring(0,cnt);
            yield return new WaitForSeconds(0.1f);
            cnt++;
        }
         
    }
    public void Cancel()
    {
        PopUpDialog.SetActive(false);
        BackHome();
    }
}
