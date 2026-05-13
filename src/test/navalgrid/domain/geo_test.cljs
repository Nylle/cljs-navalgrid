(ns navalgrid.domain.geo-test
  (:require [cljs.test :refer [deftest is testing]]
            [navalgrid.math :as math]
            [navalgrid.domain.geo :as sut]))

(deftest rhumb-distance-test
  (testing "Nautical miles from Baghdad to Osaka on a rhumb line"
    (is (= 4426 (math/round (sut/rhumb-distance [35 45] [35 135])))))
  (testing "Nautical miles on the equator"
    (is (= 120 (math/round (sut/rhumb-distance [0 1] [0 -1]))))
    (is (= 600 (math/round (sut/rhumb-distance [0 5] [0 -5]))))
    (is (= 120 (math/round (sut/rhumb-distance [0 -1] [0 1]))))
    (is (= 600 (math/round (sut/rhumb-distance [0 -5] [0 5]))))))

(deftest haversine-distance-test
  (testing "Nautical miles from Baghdad to Osaka on a great-circle line"
    (is (= 4250 (math/round (sut/haversine-distance [35 45] [35 135]))))))

(deftest normalize-180-test
  (is (= -179 (sut/normalize-180 181)))
  (is (= 179 (sut/normalize-180 -181)))
  (is (= 90 (sut/normalize-180 90))))

(deftest normalize-360-test
  (is (= 181 (sut/normalize-360 181)))
  (is (= 350 (sut/normalize-360 -10)))
  (is (= 0 (sut/normalize-360 360))))

(deftest lon-range-test
  (is (= [175 177 179 -179 -177 -175] (sut/lon-range 175 -175 5))))

(deftest lat-range-test
  (is (= [2 0 -2 -4] (sut/lat-range 2 -4 3))))

(deftest simple-rhumb-division-test
  (testing "Invalid bearing"
    (is (thrown-with-msg? :default
                          #"Invalid bearing from \[1 2] to \[3 4]. Must be one of 0°, 90°, 180°, 270°\."
                          (sut/simple-rhumb-division [1 2] [3 4] 1))))
  (testing "Regular case"
    (is (= '([0 3] [0 4] [0 5] [0 6]) (sut/simple-rhumb-division [0 3] [0 6] 3)))
    (is (= '([3 0] [4 0] [5 0] [6 0]) (sut/simple-rhumb-division [3 0] [6 0] 3))))
  (testing "Latitude across the equator"
    (is (= '([3 0] [1 0] [-1 0] [-3 0]) (sut/simple-rhumb-division [3 0] [-3 0] 3)))
    (is (= '([-3 0] [-1 0] [1 0] [3 0]) (sut/simple-rhumb-division [-3 0] [3 0] 3))))
  (testing "Longitude across the meridian"
    (is (= '([0 -3] [0 -1] [0 1] [0 3]) (sut/simple-rhumb-division [0 -3] [0 3] 3)))
    (is (= '([0 3] [0 1] [0 -1] [0 -3]) (sut/simple-rhumb-division [0 3] [0 -3] 3))))
  (testing "Longitude across the anti-meridian"
    (is (= '([0 165] [0 175] [0 -175] [0 -165]) (sut/simple-rhumb-division [0 165] [0 -165] 3)))
    (is (= '([0 -165] [0 -175] [0 175] [0 165]) (sut/simple-rhumb-division [0 -165] [0 165] 3))))
  (testing "Returns itself when divided by 1"
    (is (= '([3 0] [6 0]) (sut/simple-rhumb-division [3 0] [6 0] 1))))
  (testing "Divided by 2"
    (is (= '([2 0] [4 0] [6 0]) (sut/simple-rhumb-division [2 0] [6 0] 2))))
  (testing "Returns itself when start/end are identical"
    (is (= '([0 3]) (sut/simple-rhumb-division [0 3] [0 3] 3)))
    (is (= '([3 0]) (sut/simple-rhumb-division [3 0] [3 0] 3)))))

(deftest contains-lat?-test
  (is (= true (sut/contains-lat? [[90 1] [-90 -1]] 0)))
  (is (= true (sut/contains-lat? [[90 1] [-90 -1]] -1)))
  (is (= true (sut/contains-lat? [[90 1] [-90 -1]] 1)))
  (is (= false (sut/contains-lat? [[10 1] [-10 -1]] 11)))
  (is (= false (sut/contains-lat? [[10 1] [-10 -1]] -11))))

(deftest contains-lon?-test
  (testing "Across meridian"
    (is (= true (sut/contains-lon? [[1 -10] [-1 10]] 0)))
    (is (= true (sut/contains-lon? [[1 -10] [-1 10]] -1)))
    (is (= true (sut/contains-lon? [[1 -10] [-1 10]] 1)))
    (is (= false (sut/contains-lon? [[1 -10] [-1 10]] 11)))
    (is (= false (sut/contains-lon? [[1 -10] [-1 10]] -11))))
  (testing "Across anti-meridian"
    (is (= true (sut/contains-lon? [[1 170] [-1 -170]] 180)))
    (is (= true (sut/contains-lon? [[1 170] [-1 -170]] -179)))
    (is (= true (sut/contains-lon? [[1 170] [-1 -170]] 179)))
    (is (= false (sut/contains-lon? [[1 170] [-1 -170]] 169)))
    (is (= false (sut/contains-lon? [[1 170] [-1 -170]] -169)))))
