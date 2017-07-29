(ns game.board
  (:use arcadia.core
        arcadia.linear
        game.core
        game.movement
        game.game-manager))

(def player-food-points (atom 10))

(def wall-damage 1)
(def points-per-food 10)
(def points-per-soda 20)
(def restart-level-delay 1.0)

(defn player-start! [go]
  (movement-start! go))

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

(defn player-cant-move! [player hit]
  (let [wall (.. hit transform gameObject)]
    (if (= (.tag wall) "InnerWall")
      (damage-wall! wall wall-damage)
      (.SetTrigger (.GetComponent player UnityEngine.Animator) "player-chop"))))

(defn player-attempt-move! [player x-dir y-dir]
  (do
    (swap! player-food-points dec)
    (attempt-move! player x-dir y-dir player-cant-move!)
    (check-game-over!)
    (swap! players-turn not)))

(defn player-update! [player]
  (if @players-turn
    (let [horizontal (.GetAxisRaw UnityEngine.Input "Horizontal")
          vertical (.GetAxisRaw UnityEngine.Input "Vertical")]
      (let [vertical (if (not= horizontal 0 ) 0 vertical)] ;; reset verticle if horizontal is anything
        (if (or (not= horizontal 0)
                (not= vertical 0))
          (player-attempt-move player horizontal vertical))))))

(defn check-game-over! []
  (if (<= @player-food-points 0)
    (game-over!)))

(defn lose-food! [loss]
  (do
    (.SetTrigger (.GetComponent player UnityEngine.Animator) "player-hit")
    (swap! player-food-points - loss)
    (check-game-over!)))

(defn player-on-trigger-enter-2d [player collision]
  (if (= (.tag collision) "Exit")
    (do
      (.Invoke player restart-level-delay)
      (set! (. player enabled) false))
    (if (= (.tag collision) "Food")
      (do
        (swap! player-food-points + points-per-food)
        (.SetActive (.gameObject collision) false))
      (if (= (.tag collision) "Soda")
        (do
          (swap! player-food-points + points-per-soda)
          (.SetActive (.gameObject collision) false))
        ))))
