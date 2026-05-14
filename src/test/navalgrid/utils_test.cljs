(ns navalgrid.utils-test
  (:require [cljs.test :refer [deftest is testing]]
            [navalgrid.utils :as sut]))

(deftest finite?-test
  (is (= true (sut/finite? 1)))
  (is (= true (sut/finite? 1.001)))
  (is (= false (sut/finite? "1")))
  (is (= false (sut/finite? nil))))

(deftest str->int-test
  (is (= 12 (sut/str->int "12"))))

(deftest str->float-test
  (is (= 1.2 (sut/str->float "1.2"))))

(deftest lpad-test
  (is (= "0002" (sut/lpad 2.0 4 0)))
  (is (= "0002" (sut/lpad "2" 4 "0"))))

(deftest num->str-test
  (is (= "0" (sut/num->str 0 0)))
  (is (= "0.0" (sut/num->str 0 1)))
  (is (= "0.00" (sut/num->str 0 2))))

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
