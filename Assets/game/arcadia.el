(defcustom arcadia-repl-command "ruby repl-client.rb"
  "Command to use for the Arcadia REPL into Unity.")

(defun arcadia-repl ()
  "Start repl"
  (interactive)
  (run-lisp arcadia-repl-command))

;; Repl Commands
(require 'game.core)
(use 'arcadia.core)
(use 'game.board)

(hook+ (object-named "main-camera") :update #'rotate)
(hook+ (object-named "cube")
       :on-collision-enter
       #'collisionHandlerExample)

(def cube (create-primitive :cube))

(set-state! cube :friendly? true)
(set-state! cube :health 100.0)

(state cube) ;; {:friendly? true, :health 100.0}
(state cube :friendly?) ;; true

(remove-state! cube :friendly?)
(state cube) ;; {:health 100.0}
(state cube :friendly?) ;; nil

(update-state! cube :health inc)
(state cube :health) ;; 101.0


;; Board Manager Tile Lists uning the GameObjectList helper component
(set-state! (object-named "board-manager") :wall-tiles (object-named "wall-tiles"))
(set-state! (object-named "board-manager") :floor-tiles (object-named "floor-tiles"))
(set-state! (object-named "board-manager") :food-tiles (object-named "food-tiles"))
(set-state! (object-named "board-manager") :enemy-tiles (object-named "enemy-tiles"))
(set-state! (object-named "board-manager") :outer-wall-tiles (object-named "outer-wall-tiles"))

;; Board Manager single gameobject refs, you can drag/drop the actual reference in the editor
;; after calling these in the repl, you can also set prefabs onto these
(set-state! (object-named "board-manager") :exit-ref (new UnityEngine.GameObject))
(set-state! (object-named "board-manager") :board-holder-ref (new UnityEngine.GameObject))

;; Game Manager State
(set-state! (object-named "game-manager") :board-manager (object-named "board-manager"))
(set-state! (object-named "game-manager") :level 3)

(hook+ (object-named "board-manager") :start #'board-start)
(hook+ (object-named "game-manager") :awake #'game-awake)

(set-state! (object-named "Wall1") :damage-sprite (new UnityEngine.Sprite))
(set-state! (object-named "Wall1") :hp 4)

(set-state! (object-named "Wall1") :chop-sound-1 (new UnityEngine.AudioClip))
(set-state! (object-named "Wall1") :chop-sound-2 (new UnityEngine.AudioClip))
