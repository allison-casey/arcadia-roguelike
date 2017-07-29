(ns game.board
  (:use arcadia.core
        arcadia.linear
        game.core))

(def columns 8)
(def rows 8)

(def wall-count (new-extrema 5 9))
(def food-count (new-extrema 1 5))

(defn board-setup! [board-holder floor-tiles outer-wall-tiles]
  (let [outer-wall-positions (square-outline-points -1 columns -1 rows)
        outer-wall-objects (random-selection outer-wall-tiles (+ columns columns rows rows 4))
        floor-positions (square-points 0 columns 0 rows)
        floor-objects (random-selection floor-tiles (* columns rows))]
    (do
      (set-parent-go-list! (layout-objects! outer-wall-objects outer-wall-positions)
                           board-holder)
      (set-parent-go-list! (layout-objects! floor-objects floor-positions)
                           board-holder))))

(defn setup-scene! [level board-manager]
  (let [floor-tiles (state-list board-manager :floor-tiles)
        outer-wall-tiles (state-list board-manager :outer-wall-tiles)
        wall-tiles (state-list board-manager :wall-tiles)
        food-tiles (state-list board-manager :food-tiles)
        enemy-tiles (state-list board-manager :enemy-tiles)
        exit (state board-manager :exit-ref)
        board (state board-manager :board-holder-ref)
        enemy-count (log-b level 2)
        positions (shuffle (square-points 1 (- columns 2)
                                          1 (- rows 2)))]
    (do (board-setup! board floor-tiles outer-wall-tiles)
        (layout-objects-lists!
         [(random-selection food-tiles (rand-int-extrema food-count))
          (random-selection wall-tiles (rand-int-extrema wall-count))
          (random-selection enemy-tiles enemy-count)]
         positions)
        (instantiate exit (v3 (dec columns) (dec rows) 0) (qidentity))
        )))


(comment (defn board-start [go]
           (setup-scene! 1 go)))
