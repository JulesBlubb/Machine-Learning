(ns hill-climber.core
  (:require [clojure.core.matrix :as mat]))

;;Alle Informationen auf https://clojuredocs.org/

;;Matrix Erzeugung
(defn rand-square-mat [n r]
  (mat/matrix (take n
                    (repeatedly
                     (fn []
                       (take n (repeatedly (fn [] (rand-int r)))))))))

;; Diagnale soll 0 sein, deshalb wird eine Matrix erzeugt,
;; die nur Nullen enthält bis auf die Diagonale. Diese wird von der Original Matrix abgezogen
(defn remove-diagonal [m]
  (mat/sub m
           (mat/diagonal-matrix (mat/diagonal m))))

(defn make-symmetric [m]
  (mat/add m (mat/transpose m)))

(def distances (make-symmetric (remove-diagonal (rand-square-mat 100 1000))))

;;Entfernungsberechnung
(defn get-distance [distances path]
  (apply + (map (fn [x y]
                  (get-in distances [x y]))
                path
                 (conj (vec (rest path)) (first path)))))

;;Hier werden die Städte anhand eines zufälligen Index getauscht und in new-path gespeichert
(defn mutate [path]
  (let [c (count path)
        i (rand-int c)
        j (rand-int c)]
    ;;https://clojuredocs.org/clojure.core/assoc
    (assoc path
      i (get path j)
      j (get path i))))

;;Hier wird geprüft ob der geänderte path(new-path) kleiner geworden ist
(defn change [distances path]
  (let [new-path (mutate path)
        dist (get-distance distances path)
        new-dist (get-distance distances new-path)]
    (if (< new-dist dist)
      (do (println "smaller distance: " new-dist  "!!")
          new-path)
      (do #_(println "COST: " dist)
          path))))

;;Hier wird path definiert (0-99)
(defn simulate [distances steps]
  (loop [i 0
         ;;path wird in Vektor gecastet
         path (vec (range 100))]
    (if (> i steps)
      (do (println "bester Pfad: " path)
          path)
      (recur (inc i) (change distances path)))))

(simulate distances 1e4)


;;----------------------------------------------------------------------------------


;;Übungen
(comment
  (def distances [[0 3 6 2]
                  [3 0 7 5]
                  [6 7 0 9]
                  [2 5 9 0]])

  (alength distances)

  (+ (aget distances 0 1) (aget distances 1 2) (aget distances 2 0))

  (aget distances (first [0 1 2]) (second [0 1 2]))

  (apply aget distances [(first [0 1 2]) (second [0 1 2])])

  (apply + [1 2 3])

  ;;rest returnt eine liste keinen Vektor deshalb (vec)
  (conj (vec (rest[0 1 2]))(first [0 1 2]))

  ;; fädelt zwei Coll. wie Reißverschluss zsm
  ;; (rest (cycle path)) -> map läuft bis zum Ende der kürzesten Coll, deshalb bricht cycle ab
  (let [path [0 1 2]]
    (map (fn [x y]
           (aget distances x y))
         path)
    (conj (vec (rest path)) (first path)))


  (reduce (fn [old i]
            (change distances [0 1 2 3]))
          [0 1 2 3]
          (range 10))

  (change distances [0 1 2 3])


  (get-distance distances [0 1 2 3])
  (get-distance distances (mutate [0 1 2 3]))

  (get-in {"a" [1 2 3]
           "b" "foo"}
          ["a" 1])


  (defn rand-mat [])

  (def test-mat (rand-square-mat 3 5))

  (let [n 3]
    (mat/matrix (repeat n (repeat n (rand-int 100)))))

  (take 3 (repeatedly (fn[] (rand-int 5))))

  (let [m (make-symmetric (remove-diagonal (rand-square-mat 100 1000)))]
    (mat/diagonal m)
    (= m (mat/transpose m)))


  (def example [[1 3 6 2]
                [3 2 7 5]
                [6 7 3 9]
                [2 5 9 4]])

  (mat/diagonal-matrix (mat/diagonal example))
  (mat/add example2 (mat/transpose example2))
  (def example2
    (rand-square-mat 4 5))
  example2

  (Math/exp 0))
