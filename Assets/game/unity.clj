(ns game.unity
  "Contains wrappers for some unity interop calls to make
   them more idiomatic to Clojure(and easier to type and read!), 
   as well as some helper methods that interact with the coroutiner to 
   make it easier to execute coroutines, invocations, ect from Clojure!"
  (:use arcadia.core
        arcadia.linear))

(def epsilon (. System.Single Epsilon))

(defn delta-time []
  (. UnityEngine.Time deltaTime))

(defn move-towards [start end step]
  (. UnityEngine.Vector2 (MoveTowards start end step)))

(defn activate! [go]
  (.SetActive go true))

(defn deactivate! [go]
  (.SetActive go false))

(defn set-parent-go!
  "Convenience method for parenting at the GameObject level"
  [child-go parent-go]
  (. (. child-go transform)
     (SetParent (. parent-go transform))))

(defn qidentity [] (.. UnityEngine.Quaternion identity))

(defn log-b
  "Wrapper for the Unity Mathf.Log function, calls log n to base b"
  [n b]
  (int (.. UnityEngine.Mathf (Log n b))))

(defn is-mobile? []
  (or (= (. UnityEngine.Application platform) (. UnityEngine.RuntimePlatform Android))
      (= (. UnityEngine.Application platform) (. UnityEngine.RuntimePlatform IPhonePlayer))))

(defn abs [n]
  (. UnityEngine.Mathf (Abs n)))

(defn coroutine
  "Function for running Unity Coroutines from Arcadia
   The given gameobject and value will be passed to the function f
   on every loop of the coroutine, waiting the given wait time returned by the
   given function f(0 for no wait time). This function requires that a single Corouiner component
   is added somewhere in the scene.  The given function f should return a -1
   when the coroutine should stop."
  [gameobject f value]
  (.. Coroutiner instance (runCoroutine gameobject f value)))

(defn invoke
  "Runs a Unity like Invoke on the function once with the given gameobject ref 
   and value after the specified wait-time.  Use this when you need to call a function
   once after a set delay."
  [gameobject f wait-time value]
  (.. Coroutiner instance (runInvoke gameobject f wait-time value)))

(defn sceneLoadedHook+
  "Adds a sceneLoaded event listener to the SceneManager"
  [gameobject f]
  (.. Coroutiner instance (sceneLoadedHook gameobject f)))
