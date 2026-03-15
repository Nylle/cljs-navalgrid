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
  (testing "not found"
    (is (= nil (sut/from-square-def "BLA" nil))))
  (testing "requested square was in repo as is"
    (is (= {:id "ÄG" :outer [[85.2 5] [85.2 45.5] [77.1 45.5] [77.1 5]]}
           (sut/from-square-def "ÄG" {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]})))
    (is (= {:id "OT" :outer [[33.8 167] [33.8 170.6] [25.7 170.6] [25.7 167]]}
           (sut/from-square-def "OT" {:id "OT" :nw [33.8 167] :se [25.7 170.6] :sub [[1] [4] [7]]})))
    (is (= {:id "ÄA" :outer [[60.9 -71.5] [60.9 -44.5] [59.1 -44.5] [59.1 -71.5]]}
           (sut/from-square-def "ÄA" {:id "ÄA" :nw [60.9 -71.5] :se [59.1 -44.5]})))
    (is (= {:id "AD" :outer [[69 -37.75] [69 -24.25] [60.9 -24.25] [60.9 -37.3] [59.1 -37.3] [59.1 -44.5] [66.3 -44.5] [66.3 -37.75]]}
           (sut/from-square-def "AD" {:id "AD" :poly [[69 -37.75] [69 -24.25] [60.9 -24.25] [60.9 -37.3] [59.1 -37.3] [59.1 -44.5] [66.3 -44.5] [66.3 -37.75]]})))
    (is (= {:id "AL3" :outer [[60.9 -19.3] [60.9 -15.7] [56.4 -15.7] [56.4 -19.3]]}
           (sut/from-square-def "AL3" {:id "AL3" :nw [60.9 -19.3] :se [56.4 -15.7] :so :v})))
    (is (= {:id "AL5" :outer [[56.4 -23.5] [56.4 -20.5] [53.7 -20.5] [53.7 -23.5]]}
           (sut/from-square-def "AL5" {:id "AL5" :nw [56.4 -23.5] :se [53.7 -20.5] :sub [[1 2] [4 5] [7 8]]}))))
  (testing "requested square is sub-square from regular"
    (is (= {:id "ÄG1" :outer [[85.2 5] [85.2 18.5] [82.5 18.5] [82.5 5]]}
           (sut/from-square-def "ÄG1" {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]})))
    (is (= {:id "ÄG3" :outer [[85.2 32] [85.2 45.5] [82.5 45.5] [82.5 32]]}
           (sut/from-square-def "ÄG3" {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]})))
    (is (= {:id "ÄG9999" :outer [[77.2 45] [77.2 45.5] [77.1 45.5] [77.1 45]]}
           (sut/from-square-def "ÄG9999" {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]}))))
  (testing "requested square is partial"
    (let [partial-square {:id "OT" :nw [33.8 167] :se [25.7 170.6] :sub [[1] [4] [7]]}]
      (is (= {:id "OT1" :outer [[33.8 167] [33.8 170.6] [31.1 170.6] [31.1 167]]}
             (sut/from-square-def "OT1" partial-square)))
      (is (= {:id "OT1999" :outer [[31.2 170.467] [31.2 170.6] [31.1 170.6] [31.1 170.467]]}
             (sut/from-square-def "OT1999" partial-square)))
      ))
  )
