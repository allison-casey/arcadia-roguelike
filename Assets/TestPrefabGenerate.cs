using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TestPrefabGenerate : MonoBehaviour {

	// Use this for initialization
	void Start ()
	{
		GameObject go = (GameObject)Resources.Load("Prefabs/Wall8");
		Instantiate(go, Vector3.zero, Quaternion.identity);
	}
	
	// Update is called once per frame
	void Update () {
		
	}
}
