(ns snake.game04
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener KeyEvent KeyListener)))

(def frame (doto (new JFrame "Snake Game")
             (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)))
(def x (ref 30))
(def y (ref 20))
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
                  (.fillRect g @x @y 10 10))
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

;; Control Your Dot
;; https://docs.oracle.com/en/java/javase/20/docs/api/java.desktop/java/awt/event/KeyEvent.html
;; https://docs.oracle.com/en/java/javase/20/docs/api/java.desktop/java/awt/event/KeyListener.html
;; https://clojuredocs.org/clojure.core/cond
;; Lines 4: KeyEvent KeyListener
;; Lines 10, 11: x->, y-> refs to represent direction of movement
;; Lines 16-20: Set our x->, y-> direction values based on cond(ition) of keyPressed KeyEvent(s) VK_LEFT, VK_RIGHT, VK_UP and VK_DOWN
;; Lines 22, 23: 'alter' x, y by applying the addition function to current value as a positive/negative multiple of our directions(x->, y->)
;; Line 25: Label our coordinate outputs
;; Lines 27: 'deref' shorthand
;; Lines 28, 29: KeyListener methods we don't need to use, but still need to define
;; Lines 31, 32: addKeyListener to panel
;; Line 33: Change clock tick to 250ms interval