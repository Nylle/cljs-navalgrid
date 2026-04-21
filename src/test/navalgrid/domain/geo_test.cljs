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

(deftest normalize-lon-test
  (is (= -179 (sut/normalize-lon 181))))

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

(deftest coords->str-test
  (testing "signed decimal"
    (is (= "1, 1" (sut/coords->str [1 1] :deg)))
    (is (= "-1, 1" (sut/coords->str [-1 1] :deg)))
    (is (= "1, -1" (sut/coords->str [1 -1] :deg)))
    (is (= "-1, -1" (sut/coords->str [-1 -1] :deg))))
  (testing "DD"
    (is (= "01.000°N, 001.000°E" (sut/coords->str [1 1] :dd)))
    (is (= "01.000°S, 001.000°E" (sut/coords->str [-1 1] :dd)))
    (is (= "01.000°N, 001.000°W" (sut/coords->str [1 -1] :dd)))
    (is (= "01.000°S, 001.000°W" (sut/coords->str [-1 -1] :dd))))
  (testing "DMM"
    (is (= "01°00.00'N, 001°00.00'E" (sut/coords->str [1 1] :dmm)))
    (is (= "01°00.00'S, 001°00.00'E" (sut/coords->str [-1 1] :dmm)))
    (is (= "01°00.00'N, 001°00.00'W" (sut/coords->str [1 -1] :dmm)))
    (is (= "01°00.00'S, 001°00.00'W" (sut/coords->str [-1 -1] :dmm))))
  (testing "DMS"
    (is (= "01°00'00\"N, 001°00'00\"E" (sut/coords->str [1 1] :dms)))
    (is (= "01°00'00\"S, 001°00'00\"E" (sut/coords->str [-1 1] :dms)))
    (is (= "01°00'00\"N, 001°00'00\"W" (sut/coords->str [1 -1] :dms)))
    (is (= "01°00'00\"S, 001°00'00\"W" (sut/coords->str [-1 -1] :dms))))
  (testing "Jerry's"
    (is (= "01 00N, 001 00E" (sut/coords->str [1 1] :jerry)))
    (is (= "01 00S, 001 00E" (sut/coords->str [-1 1] :jerry)))
    (is (= "01 00N, 001 00W" (sut/coords->str [1 -1] :jerry)))
    (is (= "01 00S, 001 00W" (sut/coords->str [-1 -1] :jerry)))))
