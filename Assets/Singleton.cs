using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
/**
 * Use this component on a gameobject with a unique instance name to prevent it from being destroyed when scene changes
 */
public class Singleton : MonoBehaviour {

	public static Singleton instance;

	public static HashSet<string> instanceNames = new HashSet<string>();

	public string instanceName;

	public void Awake() {
		if (instanceNames.Contains(instanceName)) {
			Destroy(gameObject);
		} else {
			instanceNames.Add(instanceName);
		}
		DontDestroyOnLoad(gameObject);
	}
}
