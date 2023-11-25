using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
public class PopUp : MonoBehaviour
{
    public GameObject Main;
    public  virtual void Show()
    {
        Main.SetActive(true);
        Main.gameObject.transform.DOScale(1, 0.5f).From(0.2f);
    }
    public void Hide()
    {
        Main.SetActive(false);
    }
}
