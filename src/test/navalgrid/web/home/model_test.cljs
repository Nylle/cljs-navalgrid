(ns navalgrid.web.home.model-test
  (:require [clojure.test :refer [deftest is testing]])
  (:require [navalgrid.web.home.model :as sut]))

(deftest str->ref-test
  (is (= "" (sut/str->ref "")))
  (is (= nil (sut/str->ref nil)))
  (is (= "AB1234" (sut/str->ref "ab 1234")))
  (is (= "AB1234" (sut/str->ref "A B 1 2 3 4")))
  (is (= "AB1234" (sut/str->ref "AB 12345678")))
  (is (= "AB1234" (sut/str->ref "A \" B % 1 & 2 $ 3 § 4"))))

(deftest with-sub-squares-test
  (is (= nil (sut/with-sub-squares nil)))
  (is (= nil (sut/with-sub-squares "")))
  (is (= nil (sut/with-sub-squares "XXX")))
  (is (= (sut/with-sub-squares "BF")
         {:id          "BF"
          :poly        [[51 -11.5] [51 3.5] [50.1 3.5] [50.1 2] [49.2 2] [49.2 0.5] [48.3 0.5] [48.3 -1] [45.6 -1] [45.6 -0.7] [42.9 -0.7] [42.9 -11.5]]
          :sub-squares [{:id "BF1" :nw [51 -11.5] :se [48.3 -7]}
                        {:id "BF2" :nw [51 -7] :se [48.3 -2.5]}
                        {:id "BF3" :poly [[51 -2.5] [51 3.5] [50.1 3.5] [50.1 2] [49.2 2] [49.2 0.5] [48.3 0.5] [48.3 -2.5]]}
                        {:id "BF4" :nw [48.3 -11.5] :se [45.6 -7]}
                        {:id "BF5" :nw [48.3 -7] :se [45.6 -4]}
                        {:id "BF6" :nw [48.3 -4] :se [45.6 -1]}
                        {:id "BF7" :nw [45.6 -11.5] :se [42.9 -7.9]}
                        {:id "BF8" :nw [45.6 -7.9] :se [42.9 -4.3]}
                        {:id "BF9" :nw [45.6 -4.3] :se [42.9 -0.7]}]
          :center      [46.95 -4]}) "polygonal")
  (is (= (sut/with-sub-squares "AK1")
         {:id          "AK1"
          :nw          [60.9 -37.3]
          :se          [56.4 -33.7]
          :so          :v
          :sub-squares [{:id "AK01" :nw [57.3 -35.5] :se [56.4 -33.7]}
                        {:id "AK11" :nw [60.9 -37.3] :se [60 -35.5]}
                        {:id "AK12" :nw [60.9 -35.5] :se [60 -33.7]}
                        {:id "AK13" :nw [60 -37.3] :se [59.1 -35.5]}
                        {:id "AK14" :nw [60 -35.5] :se [59.1 -33.7]}
                        {:id "AK15" :nw [59.1 -37.3] :se [58.2 -35.5]}
                        {:id "AK16" :nw [59.1 -35.5] :se [58.2 -33.7]}
                        {:id "AK17" :nw [58.2 -37.3] :se [57.3 -35.5]}
                        {:id "AK18" :nw [58.2 -35.5] :se [57.3 -33.7]}
                        {:id "AK19" :nw [57.3 -37.3] :se [56.4 -35.5]}]
          :center      [58.65 -35.5]}) "two-by-five"))

(deftest format-scale-test
  (is (= "25 000 000" (sut/format-scale 25000000))))
