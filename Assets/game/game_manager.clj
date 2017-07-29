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
 
(defn game-over! []
  (. (object-named "game-manager") (SetActive false)))

(defn restart! []
  (.LoadScene UnityEngine.SceneManagement.SceneManager 0))
