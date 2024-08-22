(ns breakout.game06
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
(def wall {:columns 15 :rows 8 :drop 100})
(def brick {:width (int (/ (field :width) (wall :columns)))
            :height 12})
(def game-state (atom {:paddle 270
                       :paddle-> 1
                       :x 30
                       :y 20
                       :x-> 1
                       :y-> 1
                       :wall (apply vector (take (* (wall :rows) (wall :columns)) (repeat 1)))}))

(defn border [object side & [column row]]
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
        (and (= object :ball) (= side :bottom)) (+ (ball :height) (@game-state :y))
        (and (= object :brick) (= side :left)) (* column (brick :width))
        (and (= object :brick) (= side :right)) (+ (brick :width) (* column (brick :width)))
        (and (= object :brick) (= side :top)) (+ (* row (brick :height)) (wall :drop))
        (and (= object :brick) (= side :bottom)) (+ (brick :height) (* row (brick :height)) (wall :drop))))

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
    (cond
      (< row 2) (.setColor g Color/red)
      (< row 4) (.setColor g Color/orange)
      (< row 6) (.setColor g Color/green)
      (< row 8) (.setColor g Color/yellow))
    (.fillRect g (border :brick :left column row) (border :brick :top column row) (- (brick :width) 2) (- (brick :height) 2))))

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

;; Color, Drop and Paint Wall Bricks with Border Function
;; Line 10: Add :drop key to wall map to represent how far down the wall of bricks should be painted
;; Lines 37-40, 24: Add bricks to border function; using optional column and row arguments
;; Lines 39, 40: Add wall :drop to brick :top and :bottom coordinates
;; Lines 66-69: Set brick color based on row number
;; Lines 70: Paint bricks with border function coordinates given column and row numbers