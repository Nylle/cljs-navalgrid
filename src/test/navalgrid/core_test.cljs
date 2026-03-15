(ns navalgrid.core-test
  (:require [cljs.test :refer [deftest is testing]]
            [navalgrid.core :as sut]))

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

(deftest index-of-test
  (is (= nil (sut/index-of ["a" "b" "c"] "d")))
  (is (= 0 (sut/index-of ["a" "b" "c"] "a")))
  (is (= 2 (sut/index-of ["a" "b" "c"] "c")))
  (is (= nil (sut/index-of '("a" "b" "c") "d")))
  (is (= 0 (sut/index-of '("a" "b" "c") "a")))
  (is (= 2 (sut/index-of '("a" "b" "c") "c"))))
