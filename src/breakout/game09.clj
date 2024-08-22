(ns breakout.game09
  (:import
   (javax.swing JFrame JPanel Timer JOptionPane)
   (java.awt.event ActionListener KeyListener)
   (java.awt Color)))

(def frame (doto (JFrame. "Breakout")
             (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)))
(def timer (new Timer 15 nil))
(def field {:width 600 :height 500})
(def paddle {:width 50 :height 10})
(def ball {:width 10 :height 10})
(def wall {:columns 15 :rows 8 :drop 100})
(def brick {:width (int (/ (field :width) (wall :columns)))
            :height 12})
(def game-state (atom {}))
(defn set-game-state []
  (swap! game-state assoc :paddle 270)
  (swap! game-state assoc :paddle-> 1)
  (swap! game-state assoc :x 30)
  (swap! game-state assoc :y 420)
  (swap! game-state assoc :x-> 1)
  (swap! game-state assoc :y-> -1)
  (swap! game-state assoc :wall (apply vector (take (* (wall :rows) (wall :columns)) (repeat 1))))
  (swap! game-state assoc :score 0)
  (swap! game-state assoc :remaining-turns 3))

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
  (swap! game-state update :paddle + (* 2 (@game-state :paddle->)))
  (swap! game-state update :x + (* 1 (@game-state :x->)))
  (swap! game-state update :y + (* 1 (@game-state :y->))))

(defn check-game-rules []
  (cond
    (<= (border :paddle :left) (border :field :left)) (swap! game-state assoc :paddle-> 1)
    (<= (border :field :right) (border :paddle :right)) (swap! game-state assoc :paddle-> -1)
    (<= (border :ball :left) (border :field :left)) (swap! game-state assoc :x-> 1)
    (<= (border :field :right) (border :ball :right)) (swap! game-state assoc :x-> -1)
    (<= (border :ball :top) (border :field :top)) (swap! game-state assoc :y-> 1)
    (<= (border :paddle :top) (border :ball :bottom))
    (do
      (swap! game-state assoc :y-> -1)
      (when (not (or (<= (border :paddle :left) (border :ball :left) (border :paddle :right))
                     (<= (border :ball :left) (border :paddle :left) (border :ball :right))
                     (<= (border :ball :left) (border :paddle :right) (border :ball :right))))
        (swap! game-state update :remaining-turns dec)
        (JOptionPane/showMessageDialog frame "Oops! Keep trying!")
        (when (< (@game-state :remaining-turns) 0)
          (set-game-state)
          (JOptionPane/showMessageDialog frame "Game Over")))))
  (doseq [row (range (wall :rows))
          column (range (wall :columns))]
    (let [index (+ column (* row (wall :columns)))
          brick-still-there? (= 1 (nth (@game-state :wall) index))
          ball-moving-down? (= 1 (@game-state :y->))
          ball-contacts-brick-top? (<= (border :ball :top) (border :brick :top column row) (border :ball :bottom))
          ball-inside-column? (<= (border :brick :left column row) (border :ball :left) (border :brick :right column row))
          ball-overlaps-left-brick? (<= (border :ball :left) (border :brick :right column row) (border :ball :right))
          ball-overlaps-right-brick? (<= (border :ball :left) (border :brick :left column row) (border :ball :right))
          ball-moving-up? (= -1 (@game-state :y->))
          ball-contacts-brick-bottom? (<= (border :ball :top) (border :brick :bottom column row) (border :ball :bottom))
          ball-moving-right? (= 1 (@game-state :x->))
          ball-contacts-brick-left? (<=  (border :ball :left) (border :brick :left column row) (border :ball :right))
          ball-inside-row? (<= (border :brick :top column row) (border :ball :top) (border :brick :bottom column row))
          ball-overlaps-row-top? (<= (border :ball :top) (border :brick :bottom column row) (border :ball :bottom))
          ball-overlaps-row-bottom? (<= (border :ball :top) (border :brick :top column row) (border :ball :bottom))
          ball-moving-left? (= -1 (@game-state :x->))
          ball-contacts-brick-right? (<= (border :ball :left) (border :brick :right column row) (border :ball :right))]
      (cond
        (and ball-moving-down?
             brick-still-there?
             ball-contacts-brick-top?
             (or ball-inside-column?
                 ball-overlaps-left-brick?
                 ball-overlaps-right-brick?))
        (do
          (swap! game-state assoc :y-> -1)
          (swap! game-state update :wall assoc index 0)
          (swap! game-state update :score + 1))
        (and ball-moving-up?
             brick-still-there?
             ball-contacts-brick-bottom?
             (or ball-inside-column?
                 ball-overlaps-left-brick?
                 ball-overlaps-right-brick?))
        (do
          (swap! game-state assoc :y-> 1)
          (swap! game-state update :wall assoc index 0)
          (swap! game-state update :score + 1))
        (and ball-moving-right?
             brick-still-there?
             ball-contacts-brick-left?
             (or ball-inside-row?
                 ball-overlaps-row-top?
                 ball-overlaps-row-bottom?))
        (do
          (swap! game-state assoc :x-> -1)
          (swap! game-state update :wall assoc index 0)
          (swap! game-state update :score + 1))
        (and ball-moving-left?
             brick-still-there?
             ball-contacts-brick-right?
             (or ball-inside-row?
                 ball-overlaps-row-top?
                 ball-overlaps-row-bottom?))
        (do
          (swap! game-state assoc :x-> 1)
          (swap! game-state update :wall assoc index 0)
          (swap! game-state update :score + 1))))))

(defn paint [g]
  (.setColor g Color/black)
  (.fillRect g 0 0 (field :width) (field :height))
  (.setColor g Color/lightGray)
  (.drawString g (str "Score: " (@game-state :score)) 10 20)
  (.drawString g (str "Remaining Turns: " (@game-state :remaining-turns)) 80 20)
  (.setColor g Color/blue)
  (.fillRect g (border :paddle :left) (border :paddle :top) (paddle :width) (paddle :height))
  (doseq [row (range (wall :rows))
          column (range (wall :columns))]
    (cond
      (= (nth (@game-state :wall) (+ column (* row (wall :columns)))) 0) (.setColor g Color/black)
      (< row 2) (.setColor g Color/red)
      (< row 4) (.setColor g Color/orange)
      (< row 6) (.setColor g Color/green)
      (< row 8) (.setColor g Color/yellow))
    (.fillRect g (border :brick :left column row) (border :brick :top column row) (- (brick :width) 2) (- (brick :height) 2)))
  (.setColor g Color/lightGray)
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
    (set-game-state)
    (.start timer)
    (.setSize frame (field :width) (+ 28 (field :height)))
    (.setLocation frame 1 1)
    (.setFocusable panel true)
    (.setVisible frame true)))

(game)

;; Add Paddle Contact Rule and Score
;; Lines 17-26: Change game-state initializaation to 'set-game-state' function
;; Line 25: Add :score key to game-state
;; Line 26: Add :remaining-turns key to game-state
;; Line 59: 'do' the following
;; Line 60: Make the ball move up
;; Line 61: When it is NOT the case that either the ball's borders are inside the paddle's borders 
;; Line 62: or the paddle's left border is inside the ball's borders
;; Line 63: or the paddle's right border is inside the ball's borders
;; Line 64: then decrement the remaining turns
;; Line 65: Display error dialog
;; Line 66: When the remaining turns are fewer than 0
;; Line 67: then reset the game state
;; Line 68: and show a game over dialog
;; Lines 97, 107, 117, 127: Add :score + 1 to each of the four brick contact rules
;; Line 132: Set the color to light gray
;; Line 133: Draw the score at cooridinates 10 20
;; Line 134: Draw the remaining turns at cooridnates 80 20
;; Line 168: 'set-game-state'