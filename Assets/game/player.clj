(ns game.board
  (:use arcadia.core
        arcadia.linear
        game.core))

(defn damage-wall!
  "Does the damage wall behaviour, this function is full of side-effects."
  [wall loss]
  (do
    (.. UnityEngine.SoundManager instance (RandomizeSfx
                                           (state wall :chop-sound-1)
                                           (state wall :chop-sound-2)))
    (set! (. (.GetComponent wall UnityEngine.SpriteRenderer) sprite)
          (state wall :damage-sprite))
    (update-state wall :hp (- (state wall :hp) loss))
    (if (<= (state wall :hp) 0)
      (.SetActive wall false))))
