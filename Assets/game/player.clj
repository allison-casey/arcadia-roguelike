(ns game.player
  (:use arcadia.core
        arcadia.linear
        game.core
        game.movement
        game.sound
        game.unity))

(def player-food-points (atom 100))

(def wall-damage 1)
(def points-per-food 10)
(def points-per-soda 20)
(def restart-level-delay 1.0)

(def food-text (atom nil))

(def touch-origin (atom (v2 -1 -1)))

(defn player-start! [go]
  (do
    (reset! food-text (. (object-named "FoodText") (GetComponent "Text")))
    (movement-start! go)))

(defn check-game-over! [player]
  (if (<= @player-food-points 0)
    (do
      (game-over!)
      (play-single (state player :game-over-sound))
      (.Stop (music-source)))))

(defn damage-wall!
  "Does the damage wall behaviour, this function is full of side-effects."
  [wall loss]
  (do
    (randomize-sfx [(state wall :chop-sound-1) (state wall :chop-sound-2)])
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
    (if (attempt-move! player x-dir y-dir player-cant-move!)
      (randomize-sfx [(state player :move-sound-1) (state player :move-sound-2)]))
    (check-game-over! player)
    (reset! players-turn false)))

(defn direction-mobile []
  (if (> (. UnityEngine.Input touchCount) 0)
    (let [my-touch (nth (. UnityEngine.Input touches) 0)]
      (if (= (. my-touch phase) (. UnityEngine.TouchPhase Began))
        (do
          (reset! touch-origin (. my-touch position))
          {:horizontal 0 :vertical 0})
        (if (and (= (. my-touch phase) (. UnityEngine.TouchPhase Ended))
                 (>= (. @touch-origin x) 0))
          (let [touch-end (. my-touch position)]
            (let [x (- (. touch-end x) (. @touch-origin x))
                  y (- (. touch-end y) (. @touch-origin y))]
              (reset! touch-origin (v2 -1 -1))
              (if (> (abs x) (abs y))
                {:horizontal (if (> x 0) 1 -1) :vertical 0}
                {:horizontal 0 :vertical (if (> y 0) 1 -1)})))
          {:horizontal 0 :vertical 0})))
    {:horizontal 0 :vertical 0}))

(defn direction-pc []
  (let [horizontal (int (. UnityEngine.Input (GetAxisRaw "Horizontal")))
        vertical (int (. UnityEngine.Input (GetAxisRaw "Vertical")))]
    (let [vertical (if (not= horizontal 0 ) 0 vertical)] ;; reset verticle if horizontal is anything
      {:horizontal horizontal :vertical vertical})))

(defn get-player-input []
  (if (is-mobile?)
    (direction-mobile)
    (direction-pc)))

(defn player-update! [player]
  (if @players-turn
    (let [direction (get-player-input)]
      (if (or (not= (:horizontal direction) 0)
              (not= (:vertical direction) 0))
        (player-attempt-move! player (:horizontal direction) (:vertical direction))))))

(defn lose-food! [player loss]
  (do
    (.SetTrigger (.GetComponent player UnityEngine.Animator) "player-hit")
    (swap! player-food-points - loss)
    (set! (. @food-text text) (str "-" loss " Food: " @player-food-points))
    (check-game-over! player)))

(defn player-on-trigger-enter-2d! [player collision]
  (if (= (.tag collision) "Exit")
    (do
      (invoke player #'restart! restart-level-delay nil)
      (deactivate! player))
    (if (= (.tag collision) "Food")
      (do
        (swap! player-food-points + points-per-food)
        (set! (. @food-text text) (str "+" points-per-food " Food: " @player-food-points))
        (randomize-sfx [(state player :eat-sound-1) (state player :eat-sound-2)])
        (.SetActive (.gameObject collision) false))
      (if (= (.tag collision) "Soda")
        (do
          (swap! player-food-points + points-per-soda)
          (set! (. @food-text text) (str "+" points-per-soda " Food: " @player-food-points))
          (randomize-sfx [(state player :drink-sound-1) (state player :drink-sound-2)])
          (.SetActive (.gameObject collision) false))
        ))))
