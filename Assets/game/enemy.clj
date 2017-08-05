(ns game.enemy
  (:use arcadia.core
        arcadia.linear
        game.core
        game.movement
        game.player
        game.sound))

(def target (atom nil))

(defn enemy-start! [go]
  (do
    (swap! enemies conj go)
    (reset! target (. UnityEngine.GameObject (FindGameObjectWithTag "Player")))
    (set-state! go :skip-move false)
    (movement-start! go)))

(defn enemy-cant-move! [enemy hit]
  (let [player (.. hit transform gameObject)]
    (if (= (.tag player) "Player")
      (do
        (lose-food! player (state enemy :player-damage))        
        (.SetTrigger (.GetComponent enemy UnityEngine.Animator) "enemy-attack")
        (randomize-sfx [(state enemy :attack-sound-1) (state enemy :attack-sound-2)])))))

(defn enemy-attempt-move! [enemy x-dir y-dir]
  (if (state enemy :skip-move)
    (set-state! enemy :skip-move false)
    (do
      (attempt-move! enemy x-dir y-dir enemy-cant-move!)      
      (set-state! enemy :skip-move true))))

(defn move-enemy! [enemy]
  (let [target-x-distance (abs (- (.. @target transform position x) (.. enemy transform position x)))]
    (if (< target-x-distance epsilon)
      (enemy-attempt-move! enemy
                           0
                           (if (> (.. @target transform position y)
                                  (.. enemy transform position y))
                             1 -1))
      (enemy-attempt-move! enemy
                           (if (> (.. @target transform position x)
                                  (.. enemy transform position x))
                             1 -1)
                           0))))


