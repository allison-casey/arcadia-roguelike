(ns game.game-manager
  (:use arcadia.core
        arcadia.linear
        game.core
        game.board))

(defn game-awake [go]
  (setup-scene! 1 (state go :board-manager)))
