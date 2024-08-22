(ns snake.game03
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener)))

(def frame (doto (new JFrame "Snake Game")
             (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)))
(def x (ref 30))
(def y (ref 20))

(defn game []
  (let [panel (proxy [JPanel ActionListener] []
                (actionPerformed [e]
                  (dosync (alter x inc)
                          (alter y + 1))
                  (println (deref x) (deref y))
                  (.repaint this))
                (paintComponent [g]
                  (.fillRect g (deref x) (deref y) 10 10)))]

    (.start (new Timer 50 panel))
    (.add (.getContentPane frame) panel)
    (.setSize frame 600 500)
    (.setFocusable panel true)
    (.setVisible frame true)))

(game)

;; Make Your Dot Move(the right way)
;; JFrame https://docs.oracle.com/en/java/javase/20/docs/api/java.desktop/javax/swing/JFrame.html
;; Line 7: JFrame/EXIT_ON_CLOSE to stop Timer when JFrame is closed
;; Lines 8, 9: Change x, y to 'ref's
;; Lines 14, 15: 'alter' a 'ref' by applying a function to the value it's referencing
;; Line 16, 19: 'deref' to get the value of a 'ref'