(ns navalgrid.core-test
  (:require [clojure.test :refer [deftest is testing]])
  (:require [navalgrid.core :as sut]))

(deftest finite?-test
  (is (= true (sut/finite? 1)))
  (is (= false (sut/finite? "1")))
  (is (= false (sut/finite? nil))))

(deftest seq-contains?-test
  (testing "truthy"
    (is (= true (sut/seq-contains? ["a" 2 :x] "a")))
    (is (= true (sut/seq-contains? ["a" 2 :x] 2)))
    (is (= true (sut/seq-contains? ["a" 2 :x] :x))))
  (testing "falsy"
    (is (= nil (sut/seq-contains? ["a" 2 :x] "b")))))

(deftest seq-empty?-test
  (is (= true (sut/seq-empty? [])))
  (is (= false (sut/seq-empty? [1 2]))))

(deftest round-test
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
    (is (= -25.7 (sut/round 3 -25.699999999999996)))))
