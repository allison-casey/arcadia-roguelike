(ns game.movement
  (:use arcadia.core
        arcadia.linear
        game.core
        game.unity))

(def move-time 0.1)
(def inverse-move-time (/ 1.0 move-time))
(def blocking-layer
  (bit-shift-left 1
                  (. UnityEngine.LayerMask (NameToLayer "BlockingLayer"))))

(defn movement-start! [go]
  (do
    (set-state! go :box-collider-2d (.GetComponent go UnityEngine.BoxCollider2D))
    (set-state! go :rigid-body-2d (.GetComponent go UnityEngine.Rigidbody2D))
    (set-state! go :moving false)))

(defn smooth-movement!
  "Used as a coroutine function, provides smooth object movement to given end point"
  [go end]
  (let [rb2d (. go (GetComponent UnityEngine.Rigidbody2D))]
    (. rb2d (MovePosition (move-towards (.position rb2d) end (* inverse-move-time (delta-time)))))
    (if (> (.sqrMagnitude (v2- (.position rb2d) end))
           epsilon)
      0 -1)))

(defn line-cast!
  "This function handles the awkward necessity of disabling
   the collider before doing the built in Unity linecast.
   Returns the hit obect from the linecast."
  [collider start end]
  (do
    (set! (. collider enabled) false)
    (let [hit (. UnityEngine.Physics2D (Linecast start end blocking-layer))]
      (set! (. collider enabled) true)
      hit)))

(defn move!
  "Does a linecast to see if the gameobject can
   move in the given x y direction.  If it can we 
   start a smooth-movement coroutine to move the object.
   Returns the hit result"
  [go x-dir y-dir]
  (let [collider (. go (GetComponent UnityEngine.BoxCollider2D))
        start (v2 (.. go transform position x) (.. go transform position y))
        end (v2+ (v2 (.. go transform position x) (.. go transform position y)) (v2 x-dir y-dir))]    
    (let [hit (line-cast! collider start end)]
      (if (nil? (.transform hit))
        (coroutine go #'smooth-movement! end))
      hit)))

(defn attempt-move!
  [go x-dir y-dir cant-move-fn]
  (let [hit (move! go x-dir y-dir)]
    (let [can-move (nil? (.transform hit))]
      (if (not can-move)
        (cant-move-fn go hit))
      can-move)))
