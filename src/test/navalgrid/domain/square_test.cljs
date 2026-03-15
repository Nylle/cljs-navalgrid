(ns navalgrid.domain.square-test
  (:require [clojure.test :refer [deftest is testing]]
            [navalgrid.domain.square :as sut]))

(deftest shift-test
  (testing "no shift"
    (is (= {:id "AB" :nw [5 175] :se [-5 -175]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :h 0)))
    (is (= {:id "AB" :nw [5 175] :se [-5 -175]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :v 0))))
  (testing "north"
    (is (= {:id "AB" :nw [15 175] :se [5 -175]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :v -1))))
  (testing "east"
    (is (= {:id "AB" :nw [5 -175] :se [-5 -165]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :h 1))))
  (testing "south"
    (is (= {:id "AB" :nw [-5 175] :se [-15 -175]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :v 1))))
  (testing "west"
    (is (= {:id "AB" :nw [5 165] :se [-5 175]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :h -1))))
  (testing "no shift"
    (is (= {:id "AB" :nw [5 175] :se [-5 -175]}
           (sut/shift {:id "AB" :nw [5 175] :se [-5 -175]} :h 0)))))

(deftest steps-test
  (testing "regular square with 9 sub squares"
    (is (= [0 0] (sut/steps 1 nil)))
    (is (= [1 0] (sut/steps 2 nil)))
    (is (= [2 0] (sut/steps 3 nil)))
    (is (= [0 1] (sut/steps 4 nil)))
    (is (= [1 1] (sut/steps 5 nil)))
    (is (= [2 1] (sut/steps 6 nil)))
    (is (= [0 2] (sut/steps 7 nil)))
    (is (= [1 2] (sut/steps 8 nil)))
    (is (= [2 2] (sut/steps 9 nil))))
  (testing "partial square with first row only"
    (is (= nil (sut/steps 4 [[1 2 3]])))
    (is (= [0 0] (sut/steps 1 [[1 2 3]])))
    (is (= [1 0] (sut/steps 2 [[1 2 3]])))
    (is (= [2 0] (sut/steps 3 [[1 2 3]]))))
  (testing "partial square with third row only"
    (is (= nil (sut/steps 1 [[7 8 9]])))
    (is (= [0 0] (sut/steps 7 [[7 8 9]])))
    (is (= [1 0] (sut/steps 8 [[7 8 9]])))
    (is (= [2 0] (sut/steps 9 [[7 8 9]]))))
  (testing "partial square with first column only"
    (is (= nil (sut/steps 2 [[1] [4] [7]])))
    (is (= [0 0] (sut/steps 1 [[1] [4] [7]])))
    (is (= [0 1] (sut/steps 4 [[1] [4] [7]])))
    (is (= [0 2] (sut/steps 7 [[1] [4] [7]]))))
  (testing "partial square with third column only"
    (is (= nil (sut/steps 2 [[3] [6] [9]])))
    (is (= [0 0] (sut/steps 3 [[3] [6] [9]])))
    (is (= [0 1] (sut/steps 6 [[3] [6] [9]])))
    (is (= [0 2] (sut/steps 9 [[3] [6] [9]]))))
  (testing "partial square with second and third row"
    (is (= [1 0] (sut/steps 5 [[4 5 6] [7 8 9]])))
    (is (= [1 1] (sut/steps 8 [[4 5 6] [7 8 9]]))))
  (testing "partial square with second and third column"
    (is (= [0 0] (sut/steps 2 [[2 3] [5 6] [8 9]])))
    (is (= [1 1] (sut/steps 6 [[2 3] [5 6] [8 9]])))
    (is (= [1 2] (sut/steps 9 [[2 3] [5 6] [8 9]])))))

(deftest sub-square-test
  (testing "regular square"
    (let [square {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]}]
      (is (= {:id "ÄG1" :nw [85.2 5] :se [82.5 18.5]}
             (sut/sub-square square 1)))
      (is (= {:id "ÄG9" :nw [79.8 32] :se [77.1 45.5]}
             (sut/sub-square square 9)))))
  (testing "partial square with one column"
    (let [square {:id "OT" :nw [33.8 167] :se [25.7 170.6] :sub [[1] [4] [7]]}]
      (testing "returns expected for valid sub-square 1"
        (is (= {:id "OT1" :nw [33.8 167] :se [31.1 170.6]}
               (sut/sub-square square 1))))
      (testing "returns nil for invalid sub-square 5"
        (is (= nil (sut/sub-square square 5))))))
  (testing "partial square with two columns"
    (let [square {:id "AL5" :nw [56.4 -23.5] :se [53.7 -20.5] :sub [[1 2] [4 5] [7 8]]}]
      (testing "returns expected for valid sub-square 8"
        (is (= {:id "AL58" :nw [54.6 -22] :se [53.7 -20.5]}
               (sut/sub-square square 8))))
      (testing "returns nil for invalid sub-square 3"
        (is (= nil (sut/sub-square square 3)))))))

(deftest from-square-def-test
  (testing "returns nil when not found"
    (is (= nil (sut/from-square-def "BLA" nil))))
  (testing "returns requested square as is"
    (let [expected {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]}]
      (is (= (sut/from-square-def "ÄG" expected) expected)))
    (let [expected {:id "AL3" :nw [60.9 -19.3] :se [56.4 -15.7]}]
      (is (= (sut/from-square-def "AL3" expected) expected))))
  (testing "returns requested sub-square from regular"
    (is (= (sut/from-square-def "ÄG1" {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]})
           {:id "ÄG1" :nw [85.2 5] :se [82.5 18.5]}))
    (is (= (sut/from-square-def "ÄG3" {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]})
           {:id "ÄG3" :nw [85.2 32] :se [82.5 45.5]}))
    (is (= (sut/from-square-def "ÄG9999" {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]})
           {:id "ÄG9999" :nw [77.2 45] :se [77.1 45.5]})))
  (testing "returns requested sub-square from partial"
    (let [partial-square {:id "OT" :nw [33.8 167] :se [25.7 170.6] :sub [[1] [4] [7]]}]
      (is (= {:id "OT1" :nw [33.8 167] :se [31.1 170.6]}
             (sut/from-square-def "OT1" partial-square)))
      (is (= {:id "OT1999" :nw [31.2 170.467] :se [31.1 170.6]}
             (sut/from-square-def "OT1999" partial-square))))))
