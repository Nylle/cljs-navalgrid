(ns navalgrid.map.maplibre-test
  (:require [clojure.test :refer [deftest is testing]])
  (:require [navalgrid.map.maplibre :as sut]))

(deftest meters-per-pixel-test
  (is (= 19567.879241210936 (sut/meters-per-pixel 0 3))))

(deftest get-scale-denominator-test
  (is (= 73957338.8644193 (sut/get-scale-denominator 0 3))))
