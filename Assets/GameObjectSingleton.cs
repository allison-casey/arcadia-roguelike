using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GameObjectSingleton : MonoBehaviour {

	public static GameObjectSingleton instance;

	// Use this for initialization
	void Awake () {
		if (instance == null)
			instance = this;
		else
			Destroy(gameObject);
		DontDestroyOnLoad(gameObject);
	}
}
