(ns navalgrid.domain.square-test
  (:require [clojure.test :refer [deftest is testing]]
            [navalgrid.domain.square :as sut]))

(deftest shift-test
  (testing "north"
    (is (= {:id "AB" :nw [15 175] :se [5 -175]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :v -1))))
  (testing "east"
    (is (= {:id "AB" :nw [5 -175] :se [-5 -165]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :h 1))))
  (testing "south"
    (is (= {:id "AB" :nw [-5 175] :se [-15 -175]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :v 1))))
  (testing "west"
    (is (= {:id "AB" :nw [5 165] :se [-5 175]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :h -1))))
  (testing "no shift"
    (is (= {:id "AB" :nw [5 175] :se [-5 -175]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :h 0)))))

;(deftest regular-test
;  (testing "large square"
;    (is (= {:ref {:l "AA"} :nw [1 1] :se [0 0]}
;           (sut/regular {:nw [1 1] :se [0 0]} {:l "AA"}))))
;  (testing "sub square 1"
;    (is (= {:ref {:l "ÄG" :s [1]} :nw [85.2 5] :se [82.5 18.5]}
;           (sut/regular {:nw [85.2 5] :se [77.1 45.5]} {:l "ÄG" :s [1]})))))
