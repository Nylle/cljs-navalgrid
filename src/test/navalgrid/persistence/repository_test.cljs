(ns navalgrid.persistence.repository-test
  (:require [cljs.test :refer [deftest is testing]])
  (:require [navalgrid.persistence.repository :as sut]))

(deftest find-in-group-test
  (testing "returns nil if not found"
    (let [group {:id ["A" "B" "C"]}]
      (is (= nil (sut/find-in-group group {:l "X"})))))
  (testing "returns nth square in group horizontally"
    (let [group {:id ["A" "B" "C"] :nw [2 2] :se [-2 -2] :o :h}]
      (is (= {:nw [2 2] :se [-2 -2]} (sut/find-in-group group {:l "A"})))
      (is (= {:nw [2 -2] :se [-2 -6]} (sut/find-in-group group {:l "B"})))
      (is (= {:nw [2 -6] :se [-2 -10]} (sut/find-in-group group {:l "C"})))))
  (testing "returns nth square in group vertically"
    (let [group {:id ["A" "B" "C"] :nw [2 2] :se [-2 -2] :o :v}]
      (is (= {:nw [2 2] :se [-2 -2]} (sut/find-in-group group {:l "A"})))
      (is (= {:nw [-2 2] :se [-6 -2]} (sut/find-in-group group {:l "B"})))
      (is (= {:nw [-6 2] :se [-10 -2]} (sut/find-in-group group {:l "C"})))))
  (testing "defaults to horizontal orientation"
    (let [group {:id ["A" "B" "C"] :nw [2 2] :se [-2 -2]}]
      (is (= {:nw [2 2] :se [-2 -2]} (sut/find-in-group group {:l "A"})))
      (is (= {:nw [2 -2] :se [-2 -6]} (sut/find-in-group group {:l "B"})))
      (is (= {:nw [2 -6] :se [-2 -10]} (sut/find-in-group group {:l "C"}))))))

(deftest find-by-ref-test
  (testing "not found"
    (is (= nil (sut/find-by-ref {:l "bla"}))))
  (testing "large regular squares"
    (is (= {:ref {:l "ÄG"} :nw [85.2 5] :se [77.1 45.5]}
           (sut/find-by-ref {:l "ÄG"}))))
  (testing "large partial squares"
    (is (= {:ref {:l "OT"} :nw [33.8 167] :se [25.7 170.6]}
           (sut/find-by-ref {:l "OT"})))))


