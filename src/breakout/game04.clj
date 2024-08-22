(ns breakout.game04
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener KeyListener)))

(def frame (doto (JFrame. "Breakout")
             (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)))
(def timer (new Timer 10 nil))
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
        (and (= object :paddle) (= side :right)) (+ (paddle :width) (@game-state :paddle))
        (and (= object :paddle) (= side :top)) (- (field :height) (paddle :height))
        (and (= object :paddle) (= side :bottom)) (field :height)
        (and (= object :ball) (= side :left)) (@game-state :x)
        (and (= object :ball) (= side :right)) (+ (ball :width) (@game-state :x))
        (and (= object :ball) (= side :top)) (@game-state :y)
        (and (= object :ball) (= side :bottom)) (+ (ball :height) (@game-state :y))))

(defn move-objects []
  (swap! game-state update :paddle + (* 1 (@game-state :paddle->)))
  (swap! game-state update :x + (* 1 (@game-state :x->)))
  (swap! game-state update :y + (* 1 (@game-state :y->))))

(defn check-game-rules []
  (cond
    (<= (border :paddle :left) (border :field :left)) (swap! game-state assoc :paddle-> 1)
    (<= (border :field :right) (border :paddle :right)) (swap! game-state assoc :paddle-> -1)
    (<= (border :ball :left) (border :field :left)) (swap! game-state assoc :x-> 1)
    (<= (border :field :right) (border :ball :right)) (swap! game-state assoc :x-> -1)
    (<= (border :ball :top) (border :field :top)) (swap! game-state assoc :y-> 1)
    (<= (border :paddle :top) (border :ball :bottom)) (swap! game-state assoc :y-> -1)))

(defn paint [g]
  (.fillRect g (border :paddle :left) (border :paddle :top) (paddle :width) (paddle :height))
  (.fillRect g (border :ball :left) (border :ball :top) (ball :width) (ball :height)))

(defn game []
  (let [panel (proxy [JPanel ActionListener KeyListener] []
                (keyPressed [e]
                  (swap! game-state update :paddle-> * -1))
                (actionPerformed [e]
                  (move-objects)
                  (check-game-rules)
                  (.repaint this))
                (paintComponent [g]
                  (paint g))
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

;; Refactor Functions; Adjust Step and Timer Delay
;; Line 8: change Timer delay from 250 to 10
;; Lines 33-36, 58: 'move-objects' function, change step multiple from 10 to 1
;; Lines 38-45, 57: 'check-game-rules' function
;; Lines 47-49, 62: 'paint' function