(ns snake.game08
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener KeyEvent KeyListener)
   (java.awt Color)))

(def frame (doto (JFrame. "Snake Game")
             (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)))
(def x (ref (list 300 290 280)))
(def y (ref (list 200 200 200)))
(def x-> (ref 1))
(def y-> (ref 0))
(def apple (ref (vector 500 400)))

(defn step [snake direction]
  (butlast (cons (+ (first snake) (* direction 10)) snake)))

(defn move [panel]
  (dosync (alter x step @x->)
          (alter y step @y->))
  (.repaint panel)
  (println "x:" @x "y:" @y))

(defn change-direction [e]
  (condp = (.getKeyCode e)
    KeyEvent/VK_LEFT (dosync (ref-set x-> -1) (ref-set y-> 0))
    KeyEvent/VK_RIGHT (dosync (ref-set x-> 1) (ref-set y-> 0))
    KeyEvent/VK_UP (dosync (ref-set y-> -1) (ref-set x-> 0))
    KeyEvent/VK_DOWN (dosync (ref-set y-> 1) (ref-set x-> 0))))

(defn paint [g]
  (.setColor g Color/GREEN)
  (.fillRect g 0 0 600 500)
  (.setColor g Color/RED)
  (.fillRect g (first @apple) (second @apple) 10 10)
  (.setColor g Color/BLUE)
  (doseq [[x y] (partition 2 (interleave @x @y))]
    (.fillRect g x y 10 10)))

(defn game []
  (let [panel (proxy [JPanel ActionListener KeyListener] []
                (keyPressed [e]
                  (change-direction e))
                (actionPerformed [e]
                  (move this)
                  (when (and (= (first @x) (first @apple))
                             (= (first @y) (second @apple)))
                    (dosync (ref-set apple (vector (* 10 (rand-int 59)) (* 10 (rand-int 49)))))))
                (paintComponent [g]
                  (paint g))
                (keyReleased [e])
                (keyTyped [e]))]

    (doto panel
      (.addKeyListener panel))
    (.start (Timer. 250 panel))
    (.add (.getContentPane frame) panel)
    (.setSize frame 600 500)
    (.setFocusable panel true)
    (.setVisible frame true)))

(game)

;; Functions and New Apple
;; Lines 18-22, 46: 'move' function
;; Lines 24-29, 44: 'change-direction' function
;; Lines 31-38, 50: 'paint' function
;; Lines 46-48: When snake eats apple, create new apple