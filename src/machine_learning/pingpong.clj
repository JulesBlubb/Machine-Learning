(ns pingpong
  (:import [PingPong MainFrame]))

;; Q wird initialisiert mit einem Zufälligen State und einer beliebigen action
(defn initialize-Q []
  (let [entries (for [x (range 11)
                      y (range 12)
                      z (range 11)
                      ;; 1 nach oben, -1 nach unten: gibt Richtung an
                      xV [1 -1]
                      yV [1 -1]
                      action (rand-nth [[:R :L] [:L :R]])]
                  [[x y z xV yV] action 0])]
    ;; (let [state (first arg) action (second arg) ...])
    (reduce (fn [Q [state action expected-reward]]
              (assoc-in Q [state action] expected-reward))
            {}
            entries)))

;; Q wird definiert als mutable Variable -> atom
(def Q (atom (initialize-Q)))

;; wird Q mit größter action für diesen state ausgewählt (mit epsilon-greedy)
;; zustand wird über paramterliste von java übergeben
;; return Links oder Rechts
(defn run-action [x-ball y-ball x-schlaeger, xV, yV]
  (if (< (rand) 0.001)
    (rand-nth [:L :R])
    (let [actions (@Q [x-ball y-ball x-schlaeger xV yV])]
      #_(println actions (key (apply max-key val actions)))
      (key (apply max-key val actions)))))

(key (apply max-key val {:R 0.0, :L 0.0}))


(def rewards (atom []))

(frequencies (take 1000 @rewards))

;; gib mir die letzten tausen
(frequencies (take 1000 (drop 99000 @rewards)))


(defn updateQ [Q reward action state new-state]
  (when-not (zero? reward)
    (swap! rewards conj reward))
  (let [alpha 0.1
        gamma 0.9
        ;; ist die action die ich früher ausgewählt hab gut?
        max-Q (apply max (vals (Q new-state)))
        old-Q ((Q state) action)
        reward-term (+ reward
                       (* gamma max-Q)
                       (- old-Q))
        new-Q (+ old-Q
                 (* alpha reward-term))]
    #_(println "FOO" old-Q new-Q)
    (assoc-in Q
              [state action]
              new-Q)))

(comment
  (let [Q {0 {:W 0.6
              :L 0.1}
           1 {:W 0.3
              :L 0.5}}]
    (updateQ Q 0 :W 0 1)))

;; game controls
(defn create-game []
  ;; atom um mutable variablen zu erzeugen
  (let [state (atom {})]
    ;; unblocked das REPL -> Runnable
    (Thread.
     ;; proxy 'beerbt' die Java-Klasse, into-array -> Default-Konstruktor
     ;; erzeuge ein Objekt das MainFrame beerbt
     (proxy [MainFrame Runnable] [(into-array [""])]
       ;; Override
       (runAction [x-ball y-ball x-schlaeger xV yV]
         (let [action (run-action x-ball y-ball x-schlaeger xV yV)]
           ;; wie in java variable state und action "neu" zuweisen
           ;; Variable wird nur gesetzt
           (swap! state assoc
                  :state [x-ball y-ball x-schlaeger xV yV]
                  :action action)
           ;; die action wird an das java programm zurück gegeben
           ({:R 1.0 :L -1.0} action)))
       (learn [reward x-ball y-ball x-schlaeger xV yV]
         ;; @state gibt mir den aktuellen Wert der Variable, also mit was runAction in Java aufgerufen wurde
         ;; ändert sich die Variable danach hab ich den alten Wert
         ;; Hier wird der Wert der Variable geholt
         (let [{:keys [state action]} @state]
           (swap! Q updateQ reward action state [x-ball y-ball x-schlaeger xV yV])))))))

(comment
  (swap! (atom 42) + 5)

  (swap! (atom 42)
         (fn [old] (+ old 5)))

  (swap! (atom 42)
         (fn [old arg1 arg2 arg3]
           (+ old arg1 arg2 arg3))
         5
         6
         7)

  (def game (create-game))
  (.start game)


  (defn make-some-example []
    (proxy [Object] []
      (toString [] "Hello, World!")))

  (.toString (make-some-example))

  (MainFrame/main (into-array [""]))

  (.runAction frame)

  (require '[clojure.reflect :refer [reflect]]
           '[clojure.pprint :refer [pprint]])

  (->> frame reflect :members (filter #(= (:name %) 'runAction)) pprint))
