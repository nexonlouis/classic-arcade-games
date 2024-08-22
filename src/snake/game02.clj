(ns snake.game02
  (:import
   (javax.swing JFrame JPanel Timer)
   (java.awt.event ActionListener)))

(def frame (doto (new JFrame "Snake Game")
             (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)))
(def x 0)
(def y 0)

(defn game []
  (let [panel (proxy [JPanel ActionListener] []
                (actionPerformed [e]
                  (def x (inc x))
                  (def y (inc y))
                  (println x y)
                  (.repaint this))
                (paintComponent [g] 
                  (.fillRect g x y 10 10)))]

    (.start (new Timer 50 panel))
    (.add (.getContentPane frame) panel)
    (.setSize frame 600 500)
    (.setFocusable panel true)
    (.setVisible frame true)))

(game)

;; Make Your Dot Move(the wrong way)
;; https://docs.oracle.com/en/java/javase/20/docs/api/java.desktop/javax/swing/Timer.html
;; https://docs.oracle.com/en/java/javase/20/docs/api/java.desktop/java/awt/event/ActionListener.html
;; Lines 8, 9, 19: Symbols x, y represent coordinate values for movement
;; Lines 3, 21: Timer(our clock tick). Every clock tick creates an action event
;; Lines 4, 13-17: ActionListener/actionPerformed to act on each action event