(ns navalgrid.domain.geo-test
  (:require [cljs.test :refer [deftest is testing]]
            [cljs.math :as math]
            [navalgrid.domain.geo :as geo]))

(deftest rhumb-distance-test
  (let [sut geo/rhumb-distance]
    (testing "Nautical miles from Baghdad to Osaka on a rhumb line"
      (is (= 4426 (math/round (sut [35 45] [35 135])))))
    (testing "Nautical miles on the equator"
      (is (= 120 (math/round (sut [0 1] [0 -1]))))
      (is (= 600 (math/round (sut [0 5] [0 -5]))))
      (is (= 120 (math/round (sut [0 -1] [0 1]))))
      (is (= 600 (math/round (sut [0 -5] [0 5])))))))

(deftest simple-rhumb-division-test
  (let [sut geo/simple-rhumb-division]
    (testing "Invalid bearing"
      (is (thrown-with-msg? :default
                            #"Invalid bearing from \[1 2] to \[3 4]. Must be one of 0°, 90°, 180°, 270°\."
                            (sut [1 2] [3 4] 1))))
    (testing "Regular case"
      (is (= '([0 3] [0 4] [0 5] [0 6]) (sut [0 3] [0 6] 3)))
      (is (= '([3 0] [4 0] [5 0] [6 0]) (sut [3 0] [6 0] 3))))
    (testing "Latitude across the equator"
      (is (= '([3 0] [1 0] [-1 0] [-3 0]) (sut [3 0] [-3 0] 3)))
      (is (= '([-3 0] [-1 0] [1 0] [3 0]) (sut [-3 0] [3 0] 3))))
    (testing "Longitude across the meridian"
      (is (= '([0 -3] [0 -1] [0 1] [0 3]) (sut [0 -3] [0 3] 3)))
      (is (= '([0 3] [0 1] [0 -1] [0 -3]) (sut [0 3] [0 -3] 3))))
    (testing "Longitude across the anti-meridian"
      (is (= '([0 165] [0 175] [0 -175] [0 -165]) (sut [0 165] [0 -165] 3)))
      (is (= '([0 -165] [0 -175] [0 175] [0 165]) (sut [0 -165] [0 165] 3))))
    (testing "Returns itself when divided by 1"
      (is (= '([3 0] [6 0]) (sut [3 0] [6 0] 1))))
    (testing "Divided by 2"
      (is (= '([2 0] [4 0] [6 0]) (sut [2 0] [6 0] 2))))
    (testing "Returns itself when start/end are identical"
      (is (= '([0 3]) (sut [0 3] [0 3] 3)))
      (is (= '([3 0]) (sut [3 0] [3 0] 3))))))

(deftest haversine-distance-test
         (let [sut geo/haversine-distance]
              (testing "Nautical miles from Baghdad to Osaka on a great-circle line"
                       (is (= 4250 (math/round (sut [35 45] [35 135])))))))
