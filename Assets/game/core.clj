(ns game.core
  (:use arcadia.core
        arcadia.linear
        game.unity))




(defn new-extrema
  "Builds a map containing a min/max range."
  [min max]
  {:minimum min :maximum max})

(defn rand-int-range [min max]
  (+ (rand-int (- max min)) min))

(defn rand-int-extrema [extrema]
  (rand-int-range (:minimum extrema) (:maximum extrema)))



(defn get-list
  "Gets the GameObject[] list from the GameObjectList helper component"
  [go-list-ref]
  (. (. go-list-ref (GetComponent "GameObjectList"))
     game_objects))

(defn state-list
  "Get the GameObject[] list from the GameObjectList 
   helper component with the state keyword"
  ([go kw]
   (get-list (state go kw))))



(defn set-parent-go-list!
  "Sets the given parent gameobject as the parent of each child gameobject in the list"
  [child-list parent]
  (doall
   (for [child child-list]
     (set-parent-go! child parent))))



;; Functions for generating the game map data

(defn random-selection
  "Takes a range of potential objects and a minima 
   maxima extrema and returns a random selection of
   non-unique objects within the extremes"
  [objects quantity]
  (let [object-count quantity] ;; Random count
    (loop [total 0 result []]
      (if (or (>= total object-count) (= object-count 0))
        result             
        (recur (inc total) (conj result (nth objects (rand-int (count objects)))))))))

(defn line-points
  "Basic function to generate a line of points"
  [start-x start-y inc-x inc-y length]
  (into [] (for [i (range length)]
             (v3 (+ (* inc-x i) start-x)
                 (+ (* inc-y i) start-y) 0))))

(defn square-outline-points
  "Generates a set of points on the outside edges of a square"
  [min-x max-x min-y max-y]
  (let [width (inc (- max-x min-x))
        height (inc (- max-y min-y))]
    (into [] (concat (line-points min-x max-y 1 0 width) ;; top line
                     (line-points max-x (- max-y 1) 0 -1 (- height 2)) ;; right line
                     (line-points max-x min-y -1 0 width) ;; bottom line
                     (line-points min-x (+ min-y 1) 0 1 (- height 2)))))) ;; left line

(defn square-points
  "Generates a square/grid of positions given the x and y extrema/range"
  [min-x max-x min-y max-y]
  (into [] (for [col (range min-x max-x)
                 row (range min-y max-y)]
             (v3 col row 0))))



;; These two functions take the data generated for the map and instantiate each of the objects

(defn layout-objects!
  "Instantiates each GameObject at the given point, returns instantiated gameobjects"
  [object-list points]
  (loop [objects object-list
         positions points
         game-objects []]
    (if (or (= (count objects) 0) (= (count positions) 0))
      game-objects             
      (recur (pop objects)
             (pop positions)
             (conj game-objects (instantiate (last objects) (last positions) (qidentity)))))))

(defn layout-objects-lists!
  "Takes a list of game object lists and calls layout-objects on each carrying
   over the remaining points where they can be spawned on the map to each call"
  [object-lists points]
  (if (= (count object-lists) 0)
    nil
    (let [object-list (last object-lists)
          object-length (count object-list)]
      (do
        (layout-objects! object-list (subvec points 0 object-length))
        (recur (pop object-lists) (subvec points object-length))))))



;; Core state thats needed sometimes in different namespaces
(def game-manager (atom nil))
(def level-text (atom nil))
(def level-image (atom nil))
(def enemies (atom []))

(def players-turn (atom true))
(def doing-setup (atom true))
(def level (atom 0))




(defn restart! [go value]
  (.. UnityEngine.SceneManagement.SceneManager (LoadScene 0)))

(defn game-over! []
  (do
    (set! (. @level-text text) (str "After " @level " days, you starved."))
    (.SetActive @level-image true)
    (.SetActive (object-named "game-manager") false)))
