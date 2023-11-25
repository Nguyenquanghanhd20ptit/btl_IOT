using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using DG.Tweening;
public class Dialog : MonoBehaviour
{
    public static Dialog instance;
    public TMP_Text content;
    public GameObject main; 
    private void Awake()
    {
        instance = this;
    }
    public void ShowDiaLog(string data="")
    {
        if(data!="")
            content.text = data; 
        StartCoroutine(IeShowDialog());
    }
    IEnumerator IeShowDialog()
    {
        main.SetActive(true);
        yield return new WaitForSeconds(2);
        main.SetActive(false);
    }
}
