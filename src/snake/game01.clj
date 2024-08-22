(ns snake.game01
  (:import
   (javax.swing JFrame JPanel)))

(def frame (doto (new JFrame "Snake Game")
             (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)))

(def game (fn []
  (let [panel (proxy [JPanel] []
                (paintComponent [g] 
                  (.fillRect g 300 250 10 10)))]

    (.setSize frame 600 500)
    (.add (.getContentPane frame) panel)
    (.setFocusable panel true)
    (.setVisible frame true))))

(game)

;; Start With Just a Dot
;; Namespaces https://clojure.org/guides/learn/namespaces
;; JFrame https://docs.oracle.com/en/java/javase/20/docs/api/java.desktop/javax/swing/JFrame.html
;; JPanel https://docs.oracle.com/en/java/javase/20/docs/api/java.desktop/javax/swing/JPanel.html
;; Lines 1-3: Create namespace, import JFrame, JPanel
;; Lines 5-7: Create frame, set title, set close operation
;; Lines 8-16: Create game function that creates panel, sets frame size, adds panel to frame, sets focusable, sets visible
;; Line 18: Call game function to start game