using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
/**
 * Use this component on a gameobject to prevent it from being destroyed when scene changes
 */
public class Singleton : MonoBehaviour {

	public static Singleton instance;

	public void Awake() {
		if (instance != null) {
			Destroy(gameObject);
		} else {
			instance = this;
		}
		DontDestroyOnLoad(gameObject);
	}
}
