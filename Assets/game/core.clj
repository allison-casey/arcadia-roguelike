(ns game.core
  (:use arcadia.core
        arcadia.linear))

(def epsilon (. System.Single Epsilon))

(defn delta-time []
  (. UnityEngine.Time deltaTime))

(defn move-towards [start end step]
  (. UnityEngine.Vector2 (MoveTowards start end step)))

(defn new-extrema [min max]
  {:minimum min :maximum max})

(defn get-list
  "Gets the GameObject[] list from the GameObjectList component"
  [go-list-ref]
  (. (. go-list-ref (GetComponent "GameObjectList"))
     game_objects))

(defn set-parent-go!
  "Convenience method for parenting at the GameObject level"
  [child-go parent-go]
  (. (. child-go transform)
     (SetParent (. parent-go transform))))

(defn set-parent-go-list!
  "Same as set-parent-go! just works on vectors"
  [child-list parent]
  (doall
   (for [child child-list]
     (set-parent-go! child parent))))

(defn state-list
  "Get the GameObject[] list from the GameObjectList with state key"
  ([go kw]
   (get-list (state go kw))))

(defn qidentity [] (.. UnityEngine.Quaternion identity))

(defn rand-int-range [min max]
  (+ (rand-int (- max min)) min))

(defn rand-int-extrema [extrema]
  (rand-int-range (:minimum extrema) (:maximum extrema)))

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

(defn log-b
  "Wrapper for the Unity Mathf.Log function, calls log n to base b"
  [n b]
  (int (.. UnityEngine.Mathf (Log n b))))


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
  "Generates a square/grid of positions given the x and y extrema"
  [min-x max-x min-y max-y]
  (into [] (for [col (range min-x max-x)
                 row (range min-y max-y)]
             (v3 col row 0))))

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
  "Takes multiple object lists and calls layout-objects on each carrying
   over the remaining points to each call"
  [object-lists points]
  (if (= (count object-lists) 0)
    nil
    (let [object-list (last object-lists)
          object-length (count object-list)]
      (do
        (layout-objects! object-list (subvec points 0 object-length))
        (recur (pop object-lists) (subvec points object-length))))))


(defn coroutine
  "Function for running Unity Coroutines from Arcadia
   The given gameobject and value will be passed to the function f
   on every loop of the coroutine, waiting the given wait time between
   each iteration.  This function requires that a single Corouiner component
   is added somewhere in the scene.  The given function f should return a boolean
   of false when the coroutine should stop."
  [gameobject f wait value]
  (.. Coroutiner instance (runCoroutine gameobject f wait value)))


(defn collision-example [go collision]
  (arcadia.core/log (.. go name)
       " collided with "
       (.. collision gameObject name)
       " at velocity "
       (.. collision relativeVelocity)))

(defn rotate-example [gameobject] 
  (.. gameobject transform (Rotate 0 4 0)))


