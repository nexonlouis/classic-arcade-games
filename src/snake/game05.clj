(ns snake.game05
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener KeyEvent KeyListener)))

(def frame (doto (new JFrame "Snake Game")
             (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)))
(def x (ref (list 300 290 280)))
(def y (ref (list 200 200 200)))
(def x-> (ref 1))
(def y-> (ref 0))

(defn game []
  (let [panel (proxy [JPanel ActionListener KeyListener] []
                (keyPressed [e]
                  (cond
                    (= (.getKeyCode e) KeyEvent/VK_LEFT) (dosync (ref-set x-> -1) (ref-set y-> 0))
                    (= (.getKeyCode e) KeyEvent/VK_RIGHT) (dosync (ref-set x-> 1) (ref-set y-> 0))
                    (= (.getKeyCode e) KeyEvent/VK_UP) (dosync (ref-set y-> -1) (ref-set x-> 0))
                    (= (.getKeyCode e) KeyEvent/VK_DOWN) (dosync (ref-set y-> 1) (ref-set x-> 0))))
                (actionPerformed [e]
                  (dosync (alter x + (* 10 @x->))
                          (alter y + (* 10 @y->)))
                  (.repaint this)
                  (println "x:" @x "y:" @y))
                (paintComponent [g]
                  (doseq [[x y] (partition 2 (interleave @x @y))]
                    (.fillRect g x y 10 10)))
                (keyReleased [e])
                (keyTyped [e]))]

    (doto panel
      (.addKeyListener panel))
    (comment (.start (Timer. 250 panel)))
    (.add (.getContentPane frame) panel)
    (.setSize frame 600 500)
    (.setFocusable panel true)
    (.setVisible frame true)))

(game)

;; Turn Dot into Snake
;; https://clojuredocs.org/clojure.core/list
;; https://clojuredocs.org/clojure.core/interleave
;; https://clojuredocs.org/clojure.core/partition
;; https://clojuredocs.org/clojure.core/doseq
;; https://clojuredocs.org/clojure.core/map
;; Lines 8, 9, 27, 28: How to represent the snake? List of dots, one after the other. What does that look like for our numbers? Just numbers separated by our step value 10
;; Line 34: Comment out start of timer, so we can just see the snake not moving yet

(comment
  (let [x (list 300 290 280)]
    x)

  (doseq [x (list 300 290 280)
        y (list 200 200 200)]
    (println x y))
  
  (let [x (list 300 290 280)
        y (list 200 200 200)]
    (interleave x y)
    (partition 2 (interleave x y)))

  (let [x (list 300 290 280)
        y (list 200 200 200)]
    (doseq [[i j] (partition 2 (interleave x y))]
      (println i j)))

  (let [x (list 300 290 280)]
    (map inc x))

  (let [x (list 300 290 280)
        y (list 200 200 200)]
    (map + x y)
    (map list x y))

  (let [x (list 300 290 280)
        y (list 200 200 200)]
    (doseq [[i j] (map list x y)]
      (println i j))))