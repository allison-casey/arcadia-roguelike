(ns game.movement
  (:use arcadia.core
        arcadia.linear
        game.core))

(def move-time 0.1)
(def inverse-move-time (/ 1.0 move-time))
(def blocking-layer (. UnityEngine.LayerMask (NameToLayer "BlockingLayer")))

(defn movement-start! [go]
  (do
    (set-state! go :box-collider-2d (.GetComponent go UnityEngine.BoxCollider2D))
    (set-state! go :rigid-body-2d (.GetComponent go UnityEngine.Rigidbody2D))
    (set-state! go :moving false)))

(defn smooth-movement!
  "Used as a coroutine function, provides smooth object movement to given end point"
  [go end]
  (do
    (let [rb2d (state go :rigid-body-2d)]
      (.MovePosition rb2d (move-towards (.position rb2d) end (* inverse-move-time delta-time))))
    (> (.sqrMagnitude (v3- (.position (.transform go) end)))
       epsilon)))

(defn line-cast!
  "This function handles the awkward necessity of disabling
   the collider before doing the built in Unity linecast.
   Returns the hit obect from the linecast."
  [collider start end]
  (do
    (set! (. collider enabled) false)
    (let [hit (.Linecast Physics2D start end blocking-layer)]
      (set! (. collider enabled) true)
      hit)))

(defn move!
  "Does a linecast to see if the gameobject can
   move in the given x y direction.  If it can we 
   start a smooth-movement coroutine to move the object.
   Returns the hit result"
  [go x-dir y-dir]
  (let [collider (state go :box-collider-2d)
        start (.position (.transform go))
        end (v3+ start (v3 x-dir y-dir 0))
        hit (line-cast! collider start end)]
    (if (nil? (.transform hit))
      (coroutine go #'smooth-movement! 0 end)
      nil)
    hit))

(defn on-cant-move!
  [go hit])

(defn attempt-move!
  [go x-dir y-dir]
  (let [hit (move x-dir y-dir)]
    (if (= (.transform hit) nil)
      nil
      (on-cant-move go hit))))

(defn damage-wall!
  "Does the damage wall behaviour, this function is full of side-effects."
  [go loss]
  (do
    (.. UnityEngine.SoundManager instance (RandomizeSfx
                                           (state go :chop-sound-1)
                                           (state go :chop-sound-2)))
    (set! (. (.GetComponent go UnityEngine.SpriteRenderer) sprite)
          (state go :damage-sprite))
    (update-state go :hp (- (state go :hp) loss))
    (if (<= (state go :hp) 0)
      (.SetActive go false))))
