(ns game.player
  (:use arcadia.core
        arcadia.linear
        game.core
        game.movement))

(def player-food-points (atom 100))

(def wall-damage 1)
(def points-per-food 10)
(def points-per-soda 20)
(def restart-level-delay 1.0)

(def food-text (atom nil))

(defn player-start! [go]
  (do
    (reset! food-text (. (object-named "FoodText") (GetComponent "Text")))
    (movement-start! go)))

(defn check-game-over! []
  (if (<= @player-food-points 0)
    (game-over!)))

(defn damage-wall!
  "Does the damage wall behaviour, this function is full of side-effects."
  [wall loss]
  (do
    (comment (.. UnityEngine.SoundManager instance (RandomizeSfx
                                            (state wall :chop-sound-1)
                                            (state wall :chop-sound-2))))
    (set! (. (.GetComponent wall UnityEngine.SpriteRenderer) sprite)
          (state wall :damage-sprite))
    (set-state! wall :hp (- (state wall :hp) loss))
    (if (<= (int (state wall :hp)) 0)
      (.SetActive wall false))))

(defn player-cant-move! [player hit]
  (let [wall (.. hit transform gameObject)]
    (if (= (.tag wall) "InnerWall")
      (do
        (damage-wall! wall wall-damage)
        (.SetTrigger (.GetComponent player UnityEngine.Animator) "player-chop")))))

(defn player-attempt-move! [player x-dir y-dir]
  (do
    (swap! player-food-points dec)
    (set! (. @food-text text) (str "Food: " @player-food-points))
    (attempt-move! player x-dir y-dir player-cant-move!)
    (check-game-over!)
    (reset! players-turn false)))

(defn player-update! [player]
  (if @players-turn
    (let [horizontal (int (. UnityEngine.Input (GetAxisRaw "Horizontal")))
          vertical (int (. UnityEngine.Input (GetAxisRaw "Vertical")))]
      (let [vertical (if (not= horizontal 0 ) 0 vertical)] ;; reset verticle if horizontal is anything
        (if (or (not= horizontal 0)
                (not= vertical 0))
          (player-attempt-move! player horizontal vertical))))))

(defn lose-food! [player loss]
  (do
    (.SetTrigger (.GetComponent player UnityEngine.Animator) "player-hit")
    (swap! player-food-points - loss)
    (set! (. @food-text text) (str "-" loss " Food: " @player-food-points))
    (check-game-over!)))

(defn player-on-trigger-enter-2d! [player collision]
  (if (= (.tag collision) "Exit")
    (do
      (invoke player #'restart! restart-level-delay nil)
      (deactivate! player))
    (if (= (.tag collision) "Food")
      (do
        (swap! player-food-points + points-per-food)
        (set! (. @food-text text) (str "+" points-per-food " Food: " @player-food-points))
        (.SetActive (.gameObject collision) false))
      (if (= (.tag collision) "Soda")
        (do
          (swap! player-food-points + points-per-soda)
          (set! (. @food-text text) (str "+" points-per-soda " Food: " @player-food-points))
          (.SetActive (.gameObject collision) false))
        ))))
