(ns game.game-manager
  (:use arcadia.core
        arcadia.linear
        game.core
        game.board))

(def players-turn (atom true))
(def level (atom 3))

(defn game-awake [go]
  (setup-scene! @level
                (state go :board-manager)))
 
(def game-over! []
  (set! (. (object-named "game-manager") enabled)
        false))

(def restart! []
  (.LoadScene UnityEngine.SceneManagement.SceneManager 0))
