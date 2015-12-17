(ns boersenkurs
  (:require [clojure.math.numeric-tower :as math]))

;; nulltest auf 1 setzen um bias zu erzeugen
(defn oi [time]
  (let [kurs (+ 2.0 (Math/sin (* time 0.001)))]
    kurs))

(def kurs (map oi (range 0 10000)))

(defn oj [w x]
  (apply + (map * w x)))

;; man kann Gewichte auch am Anfang auf null setzen
;; n ist die Lernrate -> Schritte in denen wir uns dem Minimum nähern
(defn deltalernregel [input target w i]
  (let [n 0.01]
    (* n
       ;; Ableitung nach wi
       (* (nth input i)
          (- target (oj w input))))))

(defn update-weights [input target weights]
  (for [i (range 1 10)]
    (deltalernregel input
                    target
                    weights
                    i)))

;; Gewichte am Anfang alle 1 gesetzt
(loop [weights (repeat 10 1)
       i 0]
  (if (not= i 9989)
    (let [new-weights (update-weights (take 10 (drop i kurs))
                                      (nth kurs (+ i 11))
                                      weights)]
      (recur (doall (map + weights new-weights)) (inc i)))
    weights))


;; Neuronen -> Output
(oj '(0.11243264986342306 0.11198990508938922 0.11154716304632939 0.11110442417698553 0.11066168892409786 0.11021895773040007 0.10977623103862313 0.1093335092914953 0.10889079293173724)
    ;; Weights
    #_'(0.11325409915477769 0.11281131259089919 0.11236852763609823 0.11192574473316001 0.11148296432486725 0.11104018685400045 0.11059741276333694 0.11015464249565068 0.10971187649371225)
    ;; Inputs
    (take 10 (drop 9988 kurs)))

(nth kurs 9999)





(comment
(update-weights (take 10 (drop 1 kurs))
                         (nth kurs (+ 1 11))
                         [1 1 1 1 1 1 1 1 1 1]))
