(ns navalgrid.domain.coords-test
  (:require [clojure.test :refer [deftest is testing]])
  (:require [navalgrid.domain.coords :as sut]))

(deftest format-coord-test
  (is (= "49°39'00\"N" (sut/format-coord 49.65 :dms true)))
  (is (= "001°09'00\"W" (sut/format-coord -1.15 :dms false))))

(deftest coords->str-test
  (testing "signed decimal"
    (is (= "1, 1" (sut/coords->str [1 1] :deg)))
    (is (= "-1, 1" (sut/coords->str [-1 1] :deg)))
    (is (= "1, -1" (sut/coords->str [1 -1] :deg)))
    (is (= "-1, -1" (sut/coords->str [-1 -1] :deg))))
  (testing "DD"
    (is (= "01.100°N, 001.100°E" (sut/coords->str [1.1 1.1] :dd)))
    (is (= "01.100°S, 001.100°E" (sut/coords->str [-1.1 1.1] :dd)))
    (is (= "01.100°N, 001.100°W" (sut/coords->str [1.1 -1.1] :dd)))
    (is (= "01.100°S, 001.100°W" (sut/coords->str [-1.1 -1.1] :dd))))
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
