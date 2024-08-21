(ns breakout.game2
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener KeyListener)))

(def frame (doto (JFrame. "Breakout")
             (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)))
(def timer (new Timer 250 nil))
(def field {:width 600 :height 500})
(def border {:bottom (field :height)})
(def game-state (atom {:paddle 270
                       :paddle-> 1
                       :x 30
                       :y 20
                       :x-> 1
                       :y-> 1}))

(defn game []
  (let [panel (proxy [JPanel ActionListener KeyListener] []
                (keyPressed [e]
                  (swap! game-state update :paddle-> * -1))
                (actionPerformed [e]
                  (swap! game-state update :paddle + (* 10 (@game-state :paddle->)))
                  (swap! game-state update :x + (* 10 (@game-state :x->)))
                  (swap! game-state update :y + (* 10 (@game-state :y->)))
                  (.repaint this)
                  (println "x:" (@game-state :x) "y:" (@game-state :y) "paddle:" (@game-state :paddle)))
                (paintComponent [g]
                  (.fillRect g (@game-state :paddle) (- (border :bottom) 10) 50 10)
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

;; Add Paddle to Bottom of Game Field
;; Line 10: start defining our borders with :bottom as the height of the field
;; Line 11: Add paddle key with x coordinate value to represent how far left or right the paddle is
;; Line 12: Add paddle direction key with integer value to represent direction of paddle movement
;; Line 16: Update y direction key to a value of 1 (ball now moves of a diagnally)
;; Line 21: Reverse paddle direction by multiplying by -1 on any key press
;; Line 23: Update paddle position by adding some multiple of paddle direction to paddle position
;; Line 29: Paint paddle at the bottom of the game field

(comment
  (.start timer)
  (.setDelay timer 2000)
  (.stop timer))