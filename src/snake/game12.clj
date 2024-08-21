(ns snake.game12
  (:import
   (javax.swing JFrame JPanel Timer JOptionPane)
   (java.awt.event ActionListener KeyEvent KeyListener)
   (java.awt Color)))

(def frame (doto (JFrame. "Snake Game")
             (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)))
(def field {:width 600 :height 500})
(def x (ref (list 300 290 280)))
(def y (ref (list 200 200 200)))
(def x-> (ref 1))
(def y-> (ref 0))
(def apple (ref (vector 500 400)))

(defn step [snake direction grow?]
  (let [whole-snake (cons (+ (first snake) (* direction 10)) snake)]
    (if grow?
      whole-snake
      (butlast whole-snake))))

(defn move [panel grow?]
  (dosync (alter x step @x-> grow?)
          (alter y step @y-> grow?))
  (.repaint panel))

(defn change-direction [e]
  (condp = (.getKeyCode e)
    KeyEvent/VK_LEFT (dosync (ref-set x-> -1) (ref-set y-> 0))
    KeyEvent/VK_RIGHT (dosync (ref-set x-> 1) (ref-set y-> 0))
    KeyEvent/VK_UP (dosync (ref-set y-> -1) (ref-set x-> 0))
    KeyEvent/VK_DOWN (dosync (ref-set y-> 1) (ref-set x-> 0))))

(defn paint [g]
  (.setColor g Color/GREEN)
  (.fillRect g 0 0 (field :width) (field :height))
  (.setColor g Color/RED)
  (.fillRect g (first @apple) (second @apple) 10 10)
  (.setColor g Color/BLUE)
  (doseq [[x y] (partition 2 (interleave @x @y))]
    (.fillRect g x y 10 10)))

(defn eat? []
  (if (and (= (first @x) (first @apple))
           (= (first @y) (second @apple)))
    (dosync (ref-set apple (vector (* (rand-int 59) 10) (* (rand-int 49) 10))))
    false))

(defn reset []
  (dosync (ref-set x (list 300 290 280))
          (ref-set y (list 200 200 200))
          (ref-set x-> 1)
          (ref-set y-> 0)))

(defn check-rules []
  (when (let [run-into-border? (or (< (field :width) (first @x)) (< (first @x) 0)
                                   (< (field :height) (first @y)) (< (first @y) 0))
              run-into-self? (and (contains? (set (rest @x)) (first @x))
                                  (contains? (set (rest @y)) (first @y)))]
          (or run-into-border? run-into-self?))
    (reset)
    (JOptionPane/showMessageDialog frame "You lose!"))
  (when (< 50 (count @x))
    (reset)
    (JOptionPane/showMessageDialog frame "You win!")))

(defn game []
  (let [panel (proxy [JPanel ActionListener KeyListener] []
                (keyPressed [e]
                  (change-direction e))
                (actionPerformed [e]
                  (move this (eat?))
                  (check-rules))
                (paintComponent [g]
                  (paint g))
                (keyReleased [e])
                (keyTyped [e]))]

    (doto panel
      (.addKeyListener panel))
    (.start (Timer. 250 panel))
    (.add (.getContentPane frame) panel)
    (.setSize frame (field :width) (field :height))
    (.setFocusable panel true)
    (.setVisible frame true)))

(game)

;; Refactor to Label Code and Combine Same Values into Variables
;; Line 7, 36, 56, 57, 85: 'field', a map of the field's dimensions
;; Line 52, 53: 'reset' x->, y->, the direction of the snake also
;; Lines 56-60: Add "labels" run-into-border? run-into-self? for code readability with 'let'