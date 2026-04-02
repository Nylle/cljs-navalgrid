(ns navalgrid.math-test
  (:require [cljs.test :refer [deftest is testing]]
            [navalgrid.math :as sut]))

(deftest round-test
  (testing "to 0 digits after decimal point"
    (is (= 46 (sut/round 0 45.554000000000014)))
    (is (= -26 (sut/round 0 -25.699999999999996))))
  (testing "to 1 digit after decimal point"
    (is (= 45.6 (sut/round 1 45.554000000000014)))
    (is (= 25.7 (sut/round 1 25.699111111111116)))
    (is (= 25.7 (sut/round 1 25.699999999999996)))
    (is (= -45.6 (sut/round 1 -45.554000000000014)))
    (is (= -25.7 (sut/round 1 -25.699111111111116)))
    (is (= -25.7 (sut/round 1 -25.699999999999996))))
  (testing "to 3 digits after decimal point"
    (is (= 45.554 (sut/round 3 45.554000000000014)))
    (is (= 25.699 (sut/round 3 25.699111111111116)))
    (is (= 25.7 (sut/round 3 25.699999999999996)))
    (is (= -45.554 (sut/round 3 -45.554000000000014)))
    (is (= -25.699 (sut/round 3 -25.699111111111116)))
    (is (= -25.7 (sut/round 3 -25.699999999999996))))
  (testing "to the digit's power counted from front, when negative"
    (is (= -444830000 (sut/round -5 -444830050.3440809)))
    (is (= -45000 (sut/round -2 -45483.3440809)))
    (is (= -44000 (sut/round -2 -44483.3440809)))
    (is (= 44000 (sut/round -2 44483.3440809)))
    (is (= 45000 (sut/round -2 45483.3440809)))
    (is (= 4400000 (sut/round -2 4448300.3440809)))
    (is (= 440000000 (sut/round -2 444830050.3440809)))
    (is (= 450000000 (sut/round -2 454830050.3440809)))
    (is (= 444830000 (sut/round -5 444830050.3440809)))
    (is (= 444840000 (sut/round -5 444835050.3440809)))))