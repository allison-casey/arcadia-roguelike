(ns game.movement
  (:use arcadia.core
        arcadia.linear
        game.core))

(def move-time 0.1)
(def inverse-move-time (/ 1.0 move-time))

(defn movement-start [go]
  (do
    (set-state! go :box-collider-2d (.GetComponent go UnityEngine.BoxCollider2D))
    (set-state! go :rigid-body-2d (.GetComponent go UnityEngine.Rigidbody2D))
    (set-state! go :moving false)))

(defn smooth-movement
  "Used as a coroutine function, provides smooth object movement to given end point"
  [go end]
  (do
    (let [rb2d (state go :rigid-body-2d)]
      (.MovePosition rb2d (move-towards (.position rb2d) end (* inverse-move-time delta-time))))
    (> (.sqrMagnitude (v3- (.position (.transform go)) end))
       epsilon)))

(defn move
  ""
  [go x-dir y-dir]
  (let [collider (state go :box-collider-2d)
        coroutiner (cmp go Coroutiner)]
    (do
      (set! (. collider enabled) false)
      (let [start (.position (.transform go))
            end (v3+ start (v3 x-dir y-dir 0))
            hit (.Linecast Physics2D start end blockingLayer)]
        (do        
          (if (not (nil? (.transform hit)))
            (.runCoroutine coroutiner go #'smooth-movement 0 end)
            nil)
          (set! (. collider enabled) true)
          hit)))))

(defn attempt-move
  [x-dir y-dir])
