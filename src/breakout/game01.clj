(ns breakout.game01
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener KeyEvent KeyListener)))

(def frame (doto (JFrame. "Breakout")
             (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)))
(def timer (Timer. 250 nil))
(def field {:width 600 :height 500})
(def game-state (atom {:x 30
                       :y 20
                       :x-> 1
                       :y-> 0}))

(defn game []
  (let [panel (proxy [JPanel ActionListener KeyListener] []
                (keyPressed [e]
                  (cond
                    (= (.getKeyCode e) KeyEvent/VK_LEFT) (do (swap! game-state assoc :x-> -1) (swap! game-state assoc :y-> 0))
                    (= (.getKeyCode e) KeyEvent/VK_RIGHT) (do (swap! game-state assoc :x-> 1) (swap! game-state assoc :y-> 0))
                    (= (.getKeyCode e) KeyEvent/VK_UP) (do (swap! game-state assoc :y-> -1) (swap! game-state assoc :x-> 0))
                    (= (.getKeyCode e) KeyEvent/VK_DOWN) (do (swap! game-state assoc :y-> 1) (swap! game-state assoc :x-> 0))))
                (actionPerformed [e]
                  (swap! game-state update :x + (* 10 (@game-state :x->)))
                  (swap! game-state update :y + (* 10 (@game-state :y->)))
                  (.repaint this)
                  (println "x:" (@game-state :x) "y:" (@game-state :y)))
                (paintComponent [g]
                  (.fillRect g (@game-state :x) (@game-state :y) 10 10))
                (keyReleased [e])
                (keyTyped [e]))]

    (doto panel
      (.addKeyListener panel))
    (doto timer
      (.addActionListener panel))
    (.add (.getContentPane frame) panel)
    (.start timer)
    (.setSize frame (field :width) (+ 28 (field :height))) 
    (.setFocusable panel true)
    (.setVisible frame true)))

(game)

;; Refs to Atoms(start with snake game4, "Control Your Dot"); Add changes from the later versions of the game
;; Line 6: Replace 'new' function call with dot notation; new game title
;; Line 7: .setDefaultCloseOperation of 'frame' to JFrame/DISPOSE_ON_CLOSE
;; Line 8: Define 'timer' globally
;; Line 9, 39: 'field' map with dimensions width and height; Actual frame size needs to be 28 pixels larger than field size to account for title bar
;; Line 10-13: Change 'refs' to 'game-state' atom, x and y coordinates, x and y direction
;; Line 19-22: Change 'ref-set' to 'swap! game-state assoc'
;; Line 24, 25: Change 'alter' to 'swap! game-state update'
;; Line 24, 25: Change 'deref' x and y directions to 'deref' game-state x and y direction keyword reference
;; Line 27, 29: Change 'deref' x and y coordinates to 'deref' game-state x and y coordinate keyword reference
;; Lines 35, 36: Add 'timer' to panel
;; Line 38: Start 'timer'

(comment
  (.start timer)
  (.setDelay timer 2000)
  (.stop timer))