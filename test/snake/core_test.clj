; Tests automatically generated; can't vouch for their correctness.

(ns snake.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [snake.core :refer [move-head step set-dir]]))


(deftest move-head-tests
  (testing "move-head basic directions"
    (is (= [5 19] (move-head [5 0] :up {:width 20 :height 20})))    ;; up decreases y, wraps around
    (is (= [5 1] (move-head [5 0] :down {:width 20 :height 20})))  ;; down increases y
    (is (= [19 5] (move-head [0 5] :left {:width 20 :height 20})))  ;; left decreases x, wraps around
    (is (= [1 5] (move-head [0 5] :right {:width 20 :height 20})))
    (is (= [5 4] (move-head [5 5] :up {:width 20 :height 20})))
    (is (= [5 6] (move-head [5 5] :down {:width 20 :height 20})))
    (is (= [4 5] (move-head [5 5] :left {:width 20 :height 20})))
    (is (= [6 5] (move-head [5 5] :right {:width 20 :height 20})))))

(deftest step-tests
  (testing "step advances snake without eating"
    (let [state {:snake [[5 5] [5 4] [5 3]]
                 :dir   :right
                 :food  [10 10]
                 :board {:width 20 :height 20}}
          next (step state)]
      (is (= [[6 5] [5 5] [5 4]] (:snake next)))
      (is (= [10 10] (:food next)))
      (is (= :right (:dir next)))
      (is (= true (:alive? next)))))

  (testing "step grows snake when eating"
    (let [state {:snake [[5 5] [5 4] [5 3]]
                 :dir   :right
                 :food  [6 5]
                 :board {:width 20 :height 20}}
          next (step state)]
      (is (= [6 5] (first (:snake next))))
      (is (= 4 (count (:snake next))))
      (is (every? #(< 0 % 21) (:food next))) ;; Food should respawn within board boundaries
      (is (= true (:alive? next)))))

  (testing "food respawn behavior with specific coordinates"
    (let [state {:snake [[5 5] [5 4]]
                 :dir   :right
                 :food  [6 5]
                 :board {:width 20 :height 20}}
          next (step state)]
      (is (every? #(< 0 % 21) (:food next)) "Food should respawn within the board boundaries after eating")))

  (testing "step kills snake when hitting itself"
    (let [state {:snake [[5 5] [5 4] [5 3] [4 3] [4 4] [5 4]] ;; Snake hitting itself
                 :dir   :right
                 :food  [10 10]
                 :board {:width 20 :height 20}}
          next (step state)]
      (is (= false (:alive? next)))))

  (testing "step kills snake when hitting wall"
    (let [state {:snake [[0 5] [19 5] [18 5]] ;; Snake hitting left wall
                 :dir   :left
                 :food  [10 10]
                 :board {:width 20 :height 20}}
          next (step state)]
      (is (= false (:alive? next))))))
(deftest set-dir-tests
  (testing "set-dir prevents 180 degree turns"
    (let [initial-state {:dir :right}]
      (is (= :right (:dir (set-dir initial-state :left)))))
    (let [initial-state {:dir :left}]
      (is (= :left (:dir (set-dir initial-state :right)))))
    (let [initial-state {:dir :up}]
      (is (= :up (:dir (set-dir initial-state :down)))))
    (let [initial-state {:dir :down}]
      (is (= :down (:dir (set-dir initial-state :up))))))

  (testing "set-dir allows other turns"
    (let [initial-state {:dir :right}]
      (is (= :up (:dir (set-dir initial-state :up)))))
    (let [initial-state {:dir :right}]
      (is (= :down (:dir (set-dir initial-state :down)))))))