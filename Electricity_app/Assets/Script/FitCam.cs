using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class FitCam : MonoBehaviour
{
    public RectTransform rec;
    // Start is called before the first frame update
    void Start()
    {
        rec =GetComponent<RectTransform>();
        var s = rec.sizeDelta;
        s = new(s.y, s.x);
        rec.sizeDelta =s;
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
