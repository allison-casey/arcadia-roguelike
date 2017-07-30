(ns game.game-manager
  (:use arcadia.core
        arcadia.linear
        game.core
        game.movement
        game.enemy
        game.board))

(def turn-delay 0.1)
(def level (atom 3))

(def enemies-moving (atom false))

(def enemies-to-move (atom []))
(def waiting-no-enemies (atom false))

(defn game-awake [go]
  (do
    (reset! enemies [])
    (setup-scene! @level
                  (state go :board-manager))))

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
               @enemies-moving))
    (coroutine go #'move-enemies! nil)))

(defn restart! []
  (.LoadScene UnityEngine.SceneManagement.SceneManager 0))
