(ns breakout.game05
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener KeyListener)
   (java.awt Color)))

(def frame (doto (JFrame. "Breakout")
             (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)))
(def timer (new Timer 10 nil))
(def field {:width 600 :height 500})
(def paddle {:width 50 :height 10})
(def ball {:width 10 :height 10})
(def wall {:columns 15 :rows 8})
(def brick {:width (int (/ (field :width) (wall :columns)))
            :height 12})
(def game-state (atom {:paddle 270
                       :paddle-> 1
                       :x 30
                       :y 20
                       :x-> 1
                       :y-> 1
                       :wall (apply vector (take (* (wall :columns) (wall :rows)) (repeat 1)))}))

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
  (.setColor g Color/black)
  (.fillRect g 0 0 (field :width) (field :height))
  (.setColor g Color/blue) 
  (.fillRect g (border :paddle :left) (border :paddle :top) (paddle :width) (paddle :height))
  (.setColor g Color/lightGray)
  (.fillRect g (border :ball :left) (border :ball :top) (ball :width) (ball :height))
  (doseq [row (range (wall :rows))
          column (range (wall :columns))]
    (.setColor g Color/gray)
    (.fillRect g (* column (brick :width)) (* row (brick :height)) (- (brick :width) 2) (- (brick :height) 2))
    (.setColor g Color/black)
    (.drawString g (str (+ column (* (wall :columns) row))) (* column (brick :width)) (+ 10 (* row (brick :height))))))

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
    (.setLocation frame 1 1)
    (.setFocusable panel true)
    (.setVisible frame true)))

(game)

;; Add Color and Build Wall
;; Line 5, 54, 56, 58: Add Color to game
;; Line 13: wall dimension map
;; Line 14, 15: brick size map; brick width is calculated from field width and wall columns
;; Line 22: vector representing the state of each brick in the wall (visible or not)
(comment
  (take 5 (repeat 1))
  (take (* (wall :columns) (wall :rows)) (repeat 1))
  (vector (take (* (wall :columns) (wall :rows)) (repeat 1)))
  (apply vector (take (* (wall :columns) (wall :rows)) (repeat 1))))
;; Lines 60-63: Paint the wall as simple sequence of brick rows and columns
;; Lines 64-65: Draw the numbered index of each brick inside to show how bricks are counted when painting
(comment
  (doseq [row (range (wall :rows))
          column (range (wall :columns))]
    (println "row:" row "column:" column "index:" (+ column (* row (wall :columns)))))
  )
;; Line 87: Set location to offset frame from left corner of screen