(ns breakout.game3
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener KeyListener)))

(def frame (doto (JFrame. "Breakout")
             (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)))
(def timer (new Timer 250 nil))
(def field {:width 600 :height 500})
(def paddle {:width 50 :height 10})
(def ball {:width 10 :height 10})
(def game-state (atom {:paddle 270
                       :paddle-> 1
                       :x 30
                       :y 20
                       :x-> 1
                       :y-> 1}))

(defn border [object side]
  (cond (and (= object :field) (= side :left)) 0
        (and (= object :field) (= side :right)) (field :width)
        (and (= object :field) (= side :top)) 0
        (and (= object :field) (= side :bottom)) (field :height)
        (and (= object :paddle) (= side :left)) (@game-state :paddle)
        (and (= object :paddle) (= side :right)) (+ (@game-state :paddle) (paddle :width))
        (and (= object :paddle) (= side :top)) (- (field :height) (paddle :height))
        (and (= object :paddle) (= side :bottom)) (field :height)
        (and (= object :ball) (= side :left)) (@game-state :x)
        (and (= object :ball) (= side :right)) (+ (@game-state :x) (ball :width))
        (and (= object :ball) (= side :top)) (@game-state :y)
        (and (= object :ball) (= side :bottom)) (+ (@game-state :y) (ball :height))))

(defn game []
  (let [panel (proxy [JPanel ActionListener KeyListener] []
                (keyPressed [e]
                  (swap! game-state update :paddle-> * -1))
                (actionPerformed [e]
                  (swap! game-state update :paddle + (* 10 (@game-state :paddle->)))
                  (swap! game-state update :x + (* 10 (@game-state :x->)))
                  (swap! game-state update :y + (* 10 (@game-state :y->)))
                  (cond
                    (<= (border :paddle :left) (border :field :left)) (swap! game-state assoc :paddle-> 1)
                    (<= (border :field :right) (border :paddle :right)) (swap! game-state assoc :paddle-> -1)
                    (<= (border :ball :left) (border :field :left)) (swap! game-state assoc :x-> 1)
                    (<= (border :field :right) (border :ball :right)) (swap! game-state assoc :x-> -1)
                    (<= (border :ball :top) (border :field :top)) (swap! game-state assoc :y-> 1)
                    (<= (border :paddle :top) (border :ball :bottom)) (swap! game-state assoc :y-> -1))
                  (.repaint this)
                  (println "x:" (@game-state :x) "y:" (@game-state :y) "paddle:" (@game-state :paddle)))
                (paintComponent [g]
                  (.fillRect g (border :paddle :left) (border :paddle :top) (paddle :width) (paddle :height))
                  (.fillRect g (border :ball :left) (border :ball :top) (ball :width) (ball :height)))
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

;; Add Border Function and Contact Rules
;; Line 10: 'paddle' size map
;; Line 11: 'ball' size map
;; Lines 19-31: 'border' function to return the border coordinates of different game objects('field', 'paddle', 'ball')
;; Lines 42, 43: Add rule to change paddle direction if it crosses left or right border of field
;; Lines 44-47: Add rules to change x and y directions if ball crosses left, right, top, or bottom border of field