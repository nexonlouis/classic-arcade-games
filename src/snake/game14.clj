(ns snake.game14
  (:import
   (javax.swing JFrame JPanel Timer JOptionPane)
   (java.awt.event ActionListener KeyEvent KeyListener)
   (java.awt Color)))

(def frame (doto (new JFrame "Snake Game")
             (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)))
(def timer (new Timer 1000 nil))
(def field {:width 600 :height 500})

(def game-state (atom {:x (list 300 290 280)
                       :y (list 200 200 200)
                       :x-> 1
                       :y-> 0
                       :apple (vector 500 400)}))

(defn step [snake direction grow?]
  (let [whole-snake (cons (+ (first snake) (* direction 10)) snake)]
    (if grow?
      whole-snake
      (butlast whole-snake))))

(defn move [grow?]
  (swap! game-state update :x step (@game-state :x->) grow?)
  (swap! game-state update :y step (@game-state :y->) grow?))

(defn change-direction [e]
  (condp = (.getKeyCode e)
    KeyEvent/VK_LEFT (do (swap! game-state assoc :x-> -1) (swap! game-state assoc :y-> 0))
    KeyEvent/VK_RIGHT (do (swap! game-state assoc :x-> 1) (swap! game-state assoc :y-> 0))
    KeyEvent/VK_UP (do (swap! game-state assoc :y-> -1) (swap! game-state assoc :x-> 0))
    KeyEvent/VK_DOWN (do (swap! game-state assoc :y-> 1) (swap! game-state assoc :x-> 0))))

(defn paint [g]
  (.setColor g Color/GREEN)
  (.fillRect g 0 0 (field :width) (field :height))
  (.setColor g Color/RED)
  (.fillRect g (first (@game-state :apple)) (second (@game-state :apple)) 10 10)
  (.setColor g Color/BLUE)
  (doseq [[x y] (partition 2 (interleave (@game-state :x) (@game-state :y)))]
    (.fillRect g x y 10 10)))

(defn eat? []
  (if (and (= (first (@game-state :x)) (first (@game-state :apple)))
           (= (first (@game-state :y)) (second (@game-state :apple))))
    (swap! game-state assoc :apple (vector (* (rand-int 59) 10) (* (rand-int 49) 10)))
    false))

(defn reset []
  (swap! game-state assoc :x (list 300 290 280))
  (swap! game-state assoc :y (list 200 200 200))
  (swap! game-state assoc :x-> 1)
  (swap! game-state assoc :y-> 0))

(defn check-rules []
  (when (let [run-into-border? (or (< (field :width) (first (@game-state :x))) (< (first (@game-state :x)) 0)
                                   (< (field :height) (first (@game-state :y))) (< (first (@game-state :y)) 0))
              run-into-self? (and (contains? (set (rest (@game-state :x))) (first (@game-state :x)))
                                  (contains? (set (rest (@game-state :y))) (first (@game-state :y))))]
          (or run-into-border? run-into-self?))
    (reset)
    (JOptionPane/showMessageDialog frame "You lose!"))
  (when (< 50 (count (@game-state :x)))
    (reset)
    (JOptionPane/showMessageDialog frame "You win!")))

(defn game []
  (let [panel (proxy [JPanel ActionListener KeyListener] []
                (keyPressed [e]
                  (change-direction e))
                (actionPerformed [e]
                  (move (eat?))
                  (check-rules)
                  (.repaint this)
                  (println (@game-state :x) (@game-state :y)))
                (paintComponent [g]
                  (paint g))
                (keyReleased [e])
                (keyTyped [e]))]

    (doto panel
      (.addKeyListener panel))
    (doto timer
      (.addActionListener panel))
    (.add (.getContentPane frame) panel)
    (.setSize frame (field :width) (+ 28 (field :height)))
    (.setLocation frame 1 1)
    (.setFocusable panel true)
    (.setVisible frame true)))

(game)
;; Refactor to Use Atom for game-state
;; Line 12-16, 39, 41, 45-46, 57-61, 64, 76: Change refs to "game-state" atom 
;; Line 25-26: Change alter to swap! game-state update
;; Line 30-33, 47, 51-54: Change alter to swap! game-state assoc

(comment
  (.start timer)
  (.setDelay timer 2000)
  (.stop timer))