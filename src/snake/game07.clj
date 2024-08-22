(ns snake.game07
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
                  (.setColor g Color/GREEN)
                  (.fillRect g 0 0 600 500)
                  (.setColor g Color/RED)
                  (.fillRect g (first @apple) (second @apple) 10 10)
                  (.setColor g Color/BLUE)
                  (doseq [[x y] (partition 2 (interleave @x @y))]
                    (.fillRect g x y 10 10)))
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

;; Add Apple and Colors
;; https://clojuredocs.org/clojure.core/vector
;; https://clojure.org/reference/java_interop#_alternative_macro_syntax
;; Lines 7, 44: Dot notation replaces 'new' function for slightly more concise syntax for creating javax.swing objects in Clojure
;; Line 11: Create apple as 'ref' to a 'vector' of two coordinate values
;; Lines 5, 32-38: Set Colors, paint background, apple and snake in color