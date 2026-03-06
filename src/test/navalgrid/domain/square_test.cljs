(ns navalgrid.domain.square-test
  (:require [clojure.test :refer [deftest is testing]]
            [navalgrid.domain.square :as square]))

(deftest shift-test
  (testing "north"
    (is (= {:nw [15 175] :se [5 -175]}
           (square/shift {:nw [5 175] :se [-5 -175]} :v -1))))
  (testing "east"
    (is (= {:nw [5 -175] :se [-5 -165]}
           (square/shift {:nw [5 175] :se [-5 -175]} :h 1))))
  (testing "south"
    (is (= {:nw [-5 175] :se [-15 -175]}
           (square/shift {:nw [5 175] :se [-5 -175]} :v 1))))
  (testing "west"
    (is (= {:nw [5 165] :se [-5 175]}
           (square/shift {:nw [5 175] :se [-5 -175]} :h -1))))
  (testing "no shift"
    (is (= {:nw [5 175] :se [-5 -175]}
           (square/shift {:nw [5 175] :se [-5 -175]} :h 0)))))
