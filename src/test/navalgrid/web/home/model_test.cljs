(ns navalgrid.web.home.model-test
  (:require [clojure.test :refer [deftest is testing]])
  (:require [navalgrid.web.home.model :as sut]))

(deftest ref?-test
  (is (= nil (sut/ref? nil)))
  (is (= nil (sut/ref? "")))
  (is (= true (sut/ref? "AB1234")))
  (is (= true (sut/ref? "ÄE")))
  (is (= false (sut/ref? "56.9")))
  (is (= false (sut/ref? "-56.9"))))

(deftest str->ref-test
  (is (= nil (sut/str->ref "")))
  (is (= nil (sut/str->ref nil)))
  (is (= nil (sut/str->ref "56.9")))
  (is (= nil (sut/str->ref "-56.9")))
  (is (= "AB1234" (sut/str->ref "ab 1234")))
  (is (= "AB1234" (sut/str->ref "A B 1 2 3 4")))
  (is (= "AB1234" (sut/str->ref "AB 12345678")))
  (is (= "AB1234" (sut/str->ref "A \" B % 1 & 2 $ 3 § 4"))))

(deftest str->coords-test
  (is (= nil (sut/str->coords nil)))
  (is (= nil (sut/str->coords "")))
  (is (= nil (sut/str->coords "AB")))
  (is (= [-56.9 56.9] (sut/str->coords "-56.9, 56.9 E"))))

(deftest explode-test
  (is (= (sut/explode {:id "BF" :poly [[51 -11.5] [51 3.5] [50.1 3.5] [50.1 2] [49.2 2] [49.2 0.5] [48.3 0.5] [48.3 -1] [45.6 -1] [45.6 -0.7] [42.9 -0.7] [42.9 -11.5]]})
         [{:id "BF1" :nw [51 -11.5] :se [48.3 -7]}
          {:id "BF2" :nw [51 -7] :se [48.3 -2.5]}
          {:id "BF3" :poly [[51 -2.5] [51 3.5] [50.1 3.5] [50.1 2] [49.2 2] [49.2 0.5] [48.3 0.5] [48.3 -2.5]]}
          {:id "BF4" :nw [48.3 -11.5] :se [45.6 -7]}
          {:id "BF5" :nw [48.3 -7] :se [45.6 -4] :sub [[1 2] [4 5] [7 8]]}
          {:id "BF6" :nw [48.3 -4] :se [45.6 -1] :sub [[1 2] [4 5] [7 8]]}
          {:id "BF7" :nw [45.6 -11.5] :se [42.9 -7.9]}
          {:id "BF8" :nw [45.6 -7.9] :se [42.9 -4.3]}
          {:id "BF9" :nw [45.6 -4.3] :se [42.9 -0.7]}]))
  (is (= (sut/explode {:id "AK1" :nw [60.9 -37.3] :se [56.4 -33.7] :so :v})
         [{:id "AK01" :nw [57.3 -35.5] :se [56.4 -33.7]}
          {:id "AK11" :nw [60.9 -37.3] :se [60 -35.5]}
          {:id "AK12" :nw [60.9 -35.5] :se [60 -33.7]}
          {:id "AK13" :nw [60 -37.3] :se [59.1 -35.5]}
          {:id "AK14" :nw [60 -35.5] :se [59.1 -33.7]}
          {:id "AK15" :nw [59.1 -37.3] :se [58.2 -35.5]}
          {:id "AK16" :nw [59.1 -35.5] :se [58.2 -33.7]}
          {:id "AK17" :nw [58.2 -37.3] :se [57.3 -35.5]}
          {:id "AK18" :nw [58.2 -35.5] :se [57.3 -33.7]}
          {:id "AK19" :nw [57.3 -37.3] :se [56.4 -35.5]}])))

(deftest explode-matching*-test
  (testing "polygonal"
    (is (= (sut/explode-matching* {:id "AK" :poly [[60.9 -37.3] [60.9 -26.5] [51 -26.5] [51 -40] [56.4 -40] [56.4 -37.3]]} [56.85 -34.6])
           [{:id "AK0155" :nw [56.9 -34.7] :se [56.8 -34.5]}])
        "coordinate is in center of AK0155")
    (is (= (sut/explode-matching* {:id "AK" :poly [[60.9 -37.3] [60.9 -26.5] [51 -26.5] [51 -40] [56.4 -40] [56.4 -37.3]]} [56.85 -34.7])
           [{:id "AK0154" :nw [56.9 -34.9] :se [56.8 -34.7]}
            {:id "AK0155" :nw [56.9 -34.7] :se [56.8 -34.5]}])
        "coordinate is at edge between AK0154 and AK0155")
    (is (= (sut/explode-matching* {:id "AK" :poly [[60.9 -37.3] [60.9 -26.5] [51 -26.5] [51 -40] [56.4 -40] [56.4 -37.3]]} [56.9 -34.7])
           [{:id "AK0151" :nw [57 -34.9] :se [56.9 -34.7]}
            {:id "AK0152" :nw [57 -34.7] :se [56.9 -34.5]}
            {:id "AK0154" :nw [56.9 -34.9] :se [56.8 -34.7]}
            {:id "AK0155" :nw [56.9 -34.7] :se [56.8 -34.5]}])
        "coordinate is at corner shared by AK0151, AK0152, AK0154, and AK0155"))
  (testing "regular"
    (is (= (sut/explode-matching* {:id "CE" :nw [42.9 -35.5] :se [34.8 -24.7]} [38.85 -30.1])
           [{:id "CE5555" :nw [38.9 -30.16667] :se [38.8 -30.03333]}])
        "coordinate is in center of CE5555")
    (is (= (sut/explode-matching* {:id "CE" :nw [42.9 -35.5] :se [34.8 -24.7]} [40.2 -31.9])
           [{:id "CE1999" :nw [40.3 -32.03333] :se [40.2 -31.9]}
            {:id "CE2777" :nw [40.3 -31.9] :se [40.2 -31.76667]}
            {:id "CE4333" :nw [40.2 -32.03333] :se [40.1 -31.9]}
            {:id "CE5111" :nw [40.2 -31.9] :se [40.1 -31.76667]}])
        "coordinate is at corner shared by CE1999, CE2777, CE4333, and CE5111")))

(deftest enrich-test
  (is (= nil (sut/enrich nil)))
  (is (= (sut/enrich {:id "AK1" :nw [60.9 -37.3] :se [56.4 -33.7] :so :v})
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
          :label       [58.65 -35.5]
          :center      [58.65 -35.5]
          :dimensions  {:height 270.18 :max-width 119.61 :min-width 105.12 :mean-width 112.36}})
      "two-by-five")
  (is (= (sut/enrich {:id "BF" :poly [[51 -11.5] [51 3.5] [50.1 3.5] [50.1 2] [49.2 2] [49.2 0.5] [48.3 0.5] [48.3 -1] [45.6 -1] [45.6 -0.7] [42.9 -0.7] [42.9 -11.5]]})
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
          :label       [46.95 -7.45]
          :center      [46.95 -4]
          :dimensions  {:height 486.32 :max-width 659.72 :min-width 566.76 :mean-width 613.24}})
      "polygonal"))

(deftest square-test
  (testing "returns nothing"
    (is (= [] (sut/square nil nil)))
    (is (= [] (sut/square "X" nil)))
    (is (= [] (sut/square nil []))))
  (testing "returns enriched square for ref"
    (is (= (sut/square "BF" nil)
           [{:id          "BF"
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
             :label       [46.95 -7.45]
             :center      [46.95 -4]
             :dimensions  {:height 486.32 :max-width 659.72 :min-width 566.76 :mean-width 613.24}}])))
  (testing "returns enriched square for coords"
    (is (= (sut/square nil [46.95 -7.45])
           [{:id          "BF4664"
             :nw          [47 -7.5]
             :se          [46.9 -7.33333]
             :sub-squares []
             :label       [46.95 -7.41666]
             :center      [46.95 -7.41666]
             :dimensions  {:height 6 :max-width 6.84 :min-width 6.82 :mean-width 6.83}}]))))

(deftest format-scale-test
  (is (= "25 000 000" (sut/format-scale 25000000))))
