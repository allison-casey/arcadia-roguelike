(ns game.game-manager
  (:use arcadia.core
        arcadia.linear
        game.core
        game.movement
        game.enemy
        game.board))

(def turn-delay 0.1)
(def level-start-delay 2.0)

(def enemies-moving (atom false))
(def waiting-no-enemies (atom false))
(def enemies-to-move (atom []))

(defn hide-level-image! [go value]
  (do
    (.SetActive @level-image false)
    (reset! doing-setup false)))

(defn init! [go]
  (do
    (reset! doing-setup true)
    (reset! level-text (. (object-named "LevelText") (GetComponent "Text")))    
    (reset! level-image (object-named "LevelImage"))
    (set! (. @level-text text) (str "Day " @level))
    (.SetActive @level-image true)
    (invoke go #'hide-level-image! level-start-delay nil)
    (reset! enemies [])
    (setup-scene! @level
                  (object-named "board-manager"))))

(defn game-on-level-was-loaded! [go index]
  (do
    (swap! level inc)
    (init! go)))

(defn game-awake [go]
  (do
    (if (= @game-manager nil)
      (do
        (reset! game-manager (object-named "game-manager"))
        (sceneLoadedHook+ @game-manager #'game-on-level-was-loaded!)))))

(defn move-enemies!
  "Used as a coroutine function, moves all enemies"
  [go v]
  (if (not @enemies-moving)
    (do
      (reset! enemies-moving true) 
      (reset! enemies-to-move @enemies)
      turn-delay) ;; initial turn delay
    (if (and (= (count @enemies) 0)
             (not @waiting-no-enemies))
      (do
        (reset! waiting-no-enemies true)
        turn-delay) ;; induce another turn delay since no enemies
      (do
        (if (> (count @enemies-to-move) 0)
          (do
            (move-enemy! (first @enemies-to-move))
            (reset! enemies-to-move (rest @enemies-to-move))
            move-time)           
          (do
            (reset! enemies-moving false)
            (reset! players-turn true)
            (reset! waiting-no-enemies false)
            -1))))))

(defn game-update! [go]
  (if (not (or @players-turn
               @enemies-moving
               @doing-setup))
    (coroutine go #'move-enemies! nil)))
