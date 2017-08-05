(ns game.sound
  (:use arcadia.core
        game.core))

(def low-pitch-range 0.95)
(def high-pitch-range 1.05)

(defn sound-source []
  (. (object-named "sound-source") (GetComponent UnityEngine.AudioSource)))
(defn music-source []
  (. (object-named "music-source") (GetComponent UnityEngine.AudioSource)))


(defn play-single [sound-clip]
  (do
    (set! (. (sound-source) clip) sound-clip)
    (.Play (sound-source))))

(defn randomize-sfx [clips]
  (do
    (set! (. (sound-source) clip)
          (nth clips (rand-int (count clips))))
    (set! (. (sound-source) pitch)
          (float (+ low-pitch-range (* (- high-pitch-range low-pitch-range) (rand)))))
    (.Play (sound-source))))
