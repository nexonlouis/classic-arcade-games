(ns snake.game06
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener KeyEvent KeyListener)))

(def frame (doto (new JFrame "Snake Game")
             (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)))
(def x (ref (list 300 290 280)))
(def y (ref (list 200 200 200)))
(def x-> (ref 1))
(def y-> (ref 0))

(defn step [snake direction]
  (butlast (cons (+ (first snake) (* direction 10)) snake)))

(defn game []
  (let [panel (proxy [JPanel ActionListener KeyListener] []
                (keyPressed [e]
                  (condp = (.getKeyCode e)
                    KeyEvent/VK_LEFT (dosync (ref-set x-> -1) (ref-set y-> 0))
                    KeyEvent/VK_RIGHT (dosync (ref-set x-> 1) (ref-set y-> 0))
                    KeyEvent/VK_UP (dosync (ref-set y-> -1) (ref-set x-> 0))
                    KeyEvent/VK_DOWN (dosync (ref-set y-> 1) (ref-set x-> 0))))
                (actionPerformed [e]
                  (dosync (alter x step @x->)
                          (alter y step @y->))
                  (.repaint this)
                  (println "x:" @x "y:" @y))
                (paintComponent [g]
                  (doseq [[x y] (partition 2 (interleave @x @y))]
                    (.fillRect g x y 10 10)))
                (keyReleased [e])
                (keyTyped [e]))]

    (doto panel
      (.addKeyListener panel))
    (.start (new Timer 250 panel))
    (.add (.getContentPane frame) panel)
    (.setSize frame 600 500)
    (.setFocusable panel true)
    (.setVisible frame true)))

(game)

;; Make the Snake Move
;; https://clojuredocs.org/clojure.core/first
;; https://clojuredocs.org/clojure.core/cons
;; https://clojuredocs.org/clojure.core/butlast
;; https://clojuredocs.org/clojure.core/condp
;; Lines 13, 14: 'step' function returns new snake, with head moved in chosen direction, with last dot removed
;; Line 19: Slightly more concise condition function
;; Lines 37 Remove comment to start Timer

(comment
  (let [snake (list 300 310 320)
        direction -1]
    (first snake)
    (+ (first snake) (* direction 10))
    (cons (+ (first snake) (* direction 10)) snake)
    (butlast (cons (+ (first snake) (* direction 10)) snake)))
  )