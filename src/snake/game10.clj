(ns snake.game10
  (:import
   (javax.swing JFrame JPanel Timer JOptionPane)
   (java.awt.event ActionListener KeyEvent KeyListener)
   (java.awt Color)))

(def frame (doto (JFrame. "Snake Game")
             (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)))
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

(defn eat? []
  (if (and (= (first @x) (first @apple))
           (= (first @y) (second @apple)))
    (dosync (ref-set apple (vector (* (rand-int 59) 10) (* (rand-int 49) 10))))
    false))

(defn game []
  (let [panel (proxy [JPanel ActionListener KeyListener] []
                (keyPressed [e]
                  (change-direction e))
                (actionPerformed [e]
                  (move this (eat?))
                  (when (or (< 600 (first @x)) (< (first @x) 0)
                            (< 500 (first @y)) (< (first @y) 0))
                    (dosync (ref-set x (list 300 290 280))
                            (ref-set y (list 200 200 200)))
                    (JOptionPane/showMessageDialog frame "You lose!")))
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

;; Add Game Rule With Lose Message
;; https://docs.oracle.com/en/java/javase/20/docs/api/java.desktop/javax/swing/JOptionPane.html
;; Line 3, 57: JOptionPane/showMessageDialog
;; Lines 53-57: When snake runs into border, reset snake position and show "You lose!" message dialog