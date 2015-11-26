(ns pingpong
  (:import [PingPong MainFrame]))

(def state (atom {:state [0 0 0]
                  :reward 0}))

  (swap! state assoc :state [x-ball y-ball x-schlaeger])


(def Q (initialize-Q)
  #_{[0 0 0] {:R 0.6
            :L 0.1}
   [0 0 1] {:R 0.3
            :L 0.5}})


(defn initialize-Q []
  (let [entries (for [x (range 11)
                      y (range 12)
                      z (range 11)
                      action [:L :R]]
                  [[x y z] action (rand)])]
    ;; (let [state (first arg) action (second arg) ...])
    (reduce (fn [Q [state action expected-reward]]
              (assoc-in Q [state action] expected-reward))
            {}
            entries)))

(defn run-action [x-ball y-ball x-schlaeger]
  (let [actions (Q [x-ball y-ball x-schlaeger])]
    (println [x-ball y-ball x-schlaeger] actions)
    ({:R 1.0 :L -1.0} (key (apply max-key val actions)))))

(run-action 0 1 0)

(get (assoc-in Q [[0 0 1] :L] 0.3) [0 0 1])

(defn updateQ [Q reward state new-state]
  (let [alpha 0.1
        action :L
        gamma 0.9
        old-Q ((Q state) action)
        reward-term (+ reward
                       (- (* gamma (apply max (vals (get Q new-state))))
                          old-Q))]
    (+ old-Q
       (* alpha reward-term))))

(defn learn [reward]
  (swap! state (fn [old]
                 (update old :reward (if reward inc dec))))
  (println "REWARDZ :D" reward))


(defn create-game []
  (Thread.
   (proxy [MainFrame Runnable] [(into-array [""])]
     (runAction [x-ball y-ball x-schlaeger]
       (run-action x-ball y-ball x-schlaeger))
     #_(learn [reward]
       (learn reward)))))


(comment
  (def game (create-game))
  (.start game)


  (defn make-some-example []
    (proxy [Object] []
      (toString [] "Hello, World!")))

  (.toString (make-some-example))

  (MainFrame/main (into-array [""]))

  (.runAction frame))




(updateQ Q 1 0 1)



(defn zustand [x-ball y-ball x-schlaeger]
  (* (+ (* (+ (* y-ball 10)
             x-ball)
          10)
       x-schlaeger)))






(comment
  (require '[clojure.reflect :refer [reflect]]
           '[clojure.pprint :refer [pprint]])

  (->> frame reflect :members (filter #(= (:name %) 'runAction)) pprint))
