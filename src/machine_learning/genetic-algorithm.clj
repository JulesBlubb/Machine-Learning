(ns genetic-algorithm.core
  (:require [clojure.core.matrix :as mat]))

;; Aufgabe 3 Genetischer Algorithmus ohne Gray
;; zufällig generierter Optimums-Bitstrang, Länge 100
;; Fitness = BitZahl - HammingDistanz(Anzahl unterschiedliche Bits)
;; Ziel möglichst gleich viele Bits


(def bitlänge 100)

;;Population enthält 10 Hypothesen
(def hypothesen-anzahl 10)

(def r 0.7)
;;Matrix mit Bitlänge 100 von 10 Vektoren
(defn rand-bit-mat [n r]
  (mat/matrix (take hypothesen-anzahl
                    (repeatedly
                     (fn []
                       (take n (repeatedly (fn [] (rand-int r)))))))))

(def population (rand-bit-mat bitlänge 2))

;; Ein zufällig generierter Genstrang
(def genstrang (take bitlänge (repeatedly (fn [] (rand-int 2)))))

;; Hamming-Distanz
(defn hamming [a b]
  (count (filter false? (map = a b))))

;; berechne die Fitness = Bitlänge - Hammingdistanz
(defn fitness [hypothese genstrang]
  ;;(println hypothese)
  (- bitlänge (hamming hypothese genstrang)))

;; berechne die Fitness für alle Hypothesen
(defn compare-fitness [population genstrang]
  (map (fn [hypo]
         (fitness hypo genstrang))
       population))

;; berechne die Wahrscheinlichkeit mit der die Individuen gewählt werden sollen
(defn probability [population genstrang]
  (map (fn [hypo] (/ (fitness hypo genstrang) (reduce + (compare-fitness population genstrang))))
       population))

;; sortiere die Hypothesen anhand der größten Wahrscheinlichkeit (größte zuerst)
;; Liste mit Wahrscheinlichkeiten und Hypothesen (Bitsträngen)
(defn sort-by-probability [population genstrang]
  ;; vergleiche die Wahrscheinlichkeit vom ersten Element mit der vom
  ;; zweiten Element first damit die Wahrscheinlichkeit verglichen wird
  (map second
       (sort (fn [a b]
               (> (first a)
                  (first b)))
             ;; Parameter der Funktion
             (map (fn [prob genstrang]
                    ;; Returnwert der Funktion
                    [prob genstrang])
                  (probability population genstrang)
                  population))))

(defn cross-over [population]
  (let [x-pos 0.3]
    (mapcat (fn [hypo1 hypo2]
              [(vec (concat
                     (drop (* x-pos 100) hypo1)
                     (take (* x-pos 100) hypo2)))
               (vec (concat
                     (take (* x-pos 100) hypo1)
                     (drop (* x-pos 100) hypo2)))])
            population (shuffle population))))

;; r ist der Anteil der durch Crossover ersetzt wird
;; wähle die Hypothesen mit der größten Wahrscheinlichkeit aus
(defn selection [r population genstrang]
  (let [select-number (* (- 1 r) (count population))
        new-generation (into population (cross-over-fittest population r (count population) genstrang))
        new-generation-prob (sort-by-fitness new-generation genstrang)]
    (take select-number (map second new-generation-prob))))

(defn mutate [hypostrang]
  (let [i (rand-int (- bitlänge 1))]
    #_(println "nur Hypostrang" hypostrang i)
    (if  (= (nth hypostrang i) 0)
      (do (assoc hypostrang i 1))
      (do (assoc hypostrang i 0)))))

(defn sample-position [population genstrang]
  (let [rnd (rand)
        probs (vec (probability population genstrang))]
    (loop [i 0
           sum (probs 0)]
      (if (> sum rnd)
        i
        (recur (inc i) (+ sum (probs i)))))))

(defn simulate [population genstrang]
  (let [p (count population)
        r 0.3
        m 0.5

        fitness (apply max (compare-fitness population genstrang))
        winner (first (sort-by-probability population genstrang))

        ;; population gesampelt nach Wahrscheinlichkeiten
        old-population (take (* (- 1 r) p)
                             (repeatedly #(nth population (sample-position population genstrang))))

        ;; Hypothesen für cross-over
        to-pair (take (* r (/ p 2))
                      (repeatedly #(nth population (sample-position population genstrang))))

        paired-population (cross-over to-pair)

        ;; Population mit Children
        old-paired-population (shuffle (into old-population paired-population))

        ;; Anzahl der zu mutierenden Hypothesen
        to-mutate (take (* m p) old-paired-population)
        mutated (map mutate to-mutate)
        new-population (concat mutated
                               (drop (* m p) old-paired-population))]
    (println fitness)
    (if (> fitness 90)
      (println "Bester" winner)
      (recur (conj (rest (shuffle new-population))
                   winner)
             genstrang))))


(count (filter false? (map = [1 0 1 1 1 0 0 0 0 0 1 0 0 0 1 0 0 0 0 0 1 0 1 1 1 0 0 1 1 0 1 0 1 1 0 0 1 1 1 0 0 0 0 1 0 0 1 1 0 1 0 1 0 0 1 1 1 0 1 1 0 0 0 1 1 0 1 1 0 0 1 0 1 1 0 0 0 1 1 1 1 1 0 0 0 0 0 1 1 1 1 0 1 0 1 1 1 1 1 1] genstrang)))

(def simulated (simulate population genstrang))

(count (map float (probability simulated genstrang)))































;;______________________________________________________________________________________________________________

(comment
  ;; gib mir die Stelle der Hypothese mit der größten Fitness
  (defn maximum-fitness-probability [hypothesen-anzahl population genstrang]
    (key (apply max-key val (zipmap
                             ;; es werden nur soviele keys erzeugt wie es Hypothesen gibt,
                             ;; weil map nur solange läuft wie die kürzeste Collection
                             ;; -> range hypothesen-anzahl
                             (vec (map {} (range hypothesen-anzahl) (iterate inc 0)))
                             (vec (probability population genstrang))))))

  (defn biggest-prob [population genstrang]
    (loop [i 10
           new-genere ()]
      (if (< i 0)
        (do (println "fertig"))
        (recur (dec i) (maximum-fitness-probability hypothesen-anzahl population genstrang)))))

  (biggest-prob population genstrang)

  (maximum-fitness-probability hypothesen-anzahl population genstrang)


  (apply max '(1 2 3 10)))
