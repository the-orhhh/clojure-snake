(ns snake.core
  (:gen-class))

(def initial-state
  {:snake [[5 3] [5 4] [5 5]]
   :dir   :right
   :food  [10 5]
   :alive? true
   :board {:width 20 :height 20}})

(defn move-head [[x y] dir board]
  (case dir
    :up     [x (mod (dec y) (:height board))]
    :down   [x (mod (inc y) (:height board))]
    :left   [(mod (dec x) (:width board)) y]
    :right  [(mod (inc x) (:width board)) y]))

(defn collision? [head snake board]
  (or (some #(= head %) (rest snake)) ;; Check for self-collision
      (not (and (<= 0 (first head) (dec (:width board)))
                (<= 0 (second head) (dec (:height board))))))) ;; Check for wall collision

(defn step [{:keys [snake dir food board] :as state}]
  (let [head        (first snake)
        new-head    (move-head head dir board)
        eats?       (= new-head food)
        new-body    (if eats?
                      (cons new-head snake)  ;; grow
                      (cons new-head (butlast snake)))
        spawn-food  (if eats?
                      [(rand-int (:width board)) (rand-int (:height board))]
                      food)
        is-alive?   (not (collision? new-head new-body board))]
    (assoc state
           :snake new-body
           :food spawn-food
           :alive? is-alive?)))

(defn set-dir [{:keys [dir] :as state} input]
  (if (and input (not (or (= [input dir] [:up :down])
                           (= [input dir] [:down :up])
                           (= [input dir] [:left :right])
                           (= [input dir] [:right :left]))))
    (assoc state :dir input)
    state))

(defn parse [input]
  (case input
    "w" :up
    "s" :down
    "a" :left
    "d" :right
    nil))

(defn render [{:keys [snake food board]}]
  (let [width  (:width board)
        height (:height board)
        empty-row (vec (repeat width ". "))
        empty-board (vec (repeat height empty-row))
        board-with-food (assoc-in empty-board [(second food) (first food)] "* ")
        board-with-snake (reduce (fn [b [x y]] (assoc-in b [y x] "# ")) board-with-food snake)]
    (doseq [row board-with-snake]
      (println (apply str row)))))

(defn -main []
  (loop [state initial-state
         t 0]
    ;(println "tick" t "state" (select-keys state [:snake :dir :food :alive?]))
    (Thread/sleep 200)
    (when (:alive? state)
      (render state)
      (let [input-str (read-line)
            state-with-input (if (and input-str (not (empty? input-str)))
                               (set-dir state (parse input-str))
                               state)]
        (recur (step state-with-input) (inc t))))))
    
