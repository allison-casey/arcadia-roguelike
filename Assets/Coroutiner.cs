using UnityEngine;
using System.Collections;
using clojure.lang;

/**
 * Allows for a simple way to execute coroutines from Arcadia
 * When update speed is not needed and performance is a concern.
 * 
 * Example Clojure Arcadia call:
 * 
 * (.runCoroutine a-coroutiner-component go #'the-fn 1.0)
 * 
 * The Clojure function should return a boolean of false to signal that the coroutine should stop
 * 
 */
public class Coroutiner : MonoBehaviour
{
	/**
	 * @param go        The gameobject context to pass to the clojure function
	 * @param fn        The clojure function
	 * @param waitTime  The wait time in seconds to pause the coroutine
	 * @param arg1      Any argument for the clojure fn
	 */
	public void runCoroutine(GameObject go, IFn fn, float waitTime, object arg1) {
		StartCoroutine(run (go, fn, waitTime, arg1));
	}

	public IEnumerator run(GameObject go, IFn fn, float waitTime, object arg1) {
		WaitForSeconds wfs = new WaitForSeconds (waitTime);
		bool continueRunning = true;
		while (continueRunning) {
			continueRunning = (bool)fn.invoke(go, arg1);
			yield return wfs;
		}
	}
}


