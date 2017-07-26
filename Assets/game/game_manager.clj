(ns game.game-manager
  (:use arcadia.core
        arcadia.linear
        game.core
        game.board))

(def level (atom 3))

(defn game-awake [go]
  (setup-scene! @level
                (state go :board-manager)))
 
