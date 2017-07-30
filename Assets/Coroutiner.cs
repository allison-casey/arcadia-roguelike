using UnityEngine;
using System;
using System.Collections;
using clojure.lang;

/**
 * Allows for a simple way to execute coroutines from Arcadia
 * When update speed is not needed and performance is a concern.
 * 
 * Example Clojure Arcadia call:
 * 
 * (coroutine go #'the-fn 1.0)
 * 
 * The Clojure function should return a number of seconds to wait or a -1 to signal that the coroutine should stop
 * 
 */
public class Coroutiner : MonoBehaviour
{
    public static Coroutiner instance;

    public void Awake() {
        if (instance != null) {
            Destroy(gameObject);
        } else {
            instance = this;
        }
        DontDestroyOnLoad(gameObject);
    }

    /**
     * @param go        The gameobject context to pass to the clojure function
     * @param fn        The clojure function
     * @param waitTime  The wait time in seconds to pause the coroutine
     * @param arg1      Any argument for the clojure fn
     */
    public void runCoroutine(GameObject go, IFn fn, object value) {
        StartCoroutine(run (go, fn, value));
    }
    
    public IEnumerator run(GameObject go, IFn fn, object value) {
        while (true) {
            object waitTime = fn.invoke(go, value);
            if (Convert.ToInt32(waitTime) == -1) { // signalled to stop
                break;
            }
            yield return new WaitForSeconds(Convert.ToSingle(waitTime));
        }
    }
}


