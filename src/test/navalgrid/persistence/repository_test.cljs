(ns navalgrid.persistence.repository-test
  (:require [cljs.test :refer [deftest is testing]])
  (:require [navalgrid.persistence.repository :as sut]))

(deftest extract-from-group-test
  (testing "returns nil if not found"
    (let [group {:ids ["A" "B" "C"]}]
      (is (= nil (sut/extract-from-group "X" group)))))
  (testing "returns nth square in group horizontally"
    (let [group {:ids ["A" "B" "C"] :nw [2 2] :se [-2 -2] :o :h}]
      (is (= {:id "A" :nw [2 2] :se [-2 -2]} (sut/extract-from-group "A" group)))
      (is (= {:id "B" :nw [2 -2] :se [-2 -6]} (sut/extract-from-group "B" group)))
      (is (= {:id "C" :nw [2 -6] :se [-2 -10]} (sut/extract-from-group "C" group)))))
  (testing "returns nth square in group vertically"
    (let [group {:ids ["A" "B" "C"] :nw [2 2] :se [-2 -2] :o :v}]
      (is (= {:id "A" :nw [2 2] :se [-2 -2]} (sut/extract-from-group "A" group)))
      (is (= {:id "B" :nw [-2 2] :se [-6 -2]} (sut/extract-from-group "B" group)))
      (is (= {:id "C" :nw [-6 2] :se [-10 -2]} (sut/extract-from-group "C" group)))))
  (testing "defaults to horizontal orientation"
    (let [group {:ids ["A" "B" "C"] :nw [2 2] :se [-2 -2]}]
      (is (= {:id "A" :nw [2 2] :se [-2 -2]} (sut/extract-from-group "A" group)))
      (is (= {:id "B" :nw [2 -2] :se [-2 -6]} (sut/extract-from-group "B" group)))
      (is (= {:id "C" :nw [2 -6] :se [-2 -10]} (sut/extract-from-group "C" group)))))
  (testing "returns square-orientation for two-by-five squares"
    (let [group {:ids ["A" "B" "C"] :nw [2 2] :se [-2 -2] :so :v}]
      (is (= {:id "C" :nw [2 -6] :se [-2 -10] :so :v} (sut/extract-from-group "C" group)))))
  (testing "returns valid sub-square ids"
    (let [group {:ids ["A" "B" "C"] :nw [2 2] :se [-2 -2] :sub [[1] [4] [7]]}]
      (is (= {:id "C" :nw [2 -6] :se [-2 -10] :sub [[1] [4] [7]]} (sut/extract-from-group "C" group))))))

(deftest find-by-id-test
  (testing "not found"
    (is (= nil (sut/find-by-id {:l "bla"}))))
  (testing "large regular squares"
    (is (= {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]}
           (sut/find-by-id "ÄG"))))
  (testing "large partial squares"
    (is (= {:id "OT" :nw [33.8 167] :se [25.7 170.6] :sub [[1] [4] [7]]}
           (sut/find-by-id "OT"))))
  (testing "irregular squares"
    (is (= {:id "ÄA" :nw [60.9 -71.5] :se [59.1 -44.5]}
           (sut/find-by-id "ÄA"))))
  (testing "polygonal squares"
    (is (= {:id "AD" :poly [[69 -37.75] [69 -24.25] [60.9 -24.25] [60.9 -37.3] [59.1 -37.3] [59.1 -44.5] [66.3 -44.5] [66.3 -37.75]]}
           (sut/find-by-id "AD"))))
  (testing "two-by-five squares"
    (is (= {:id "AL3" :nw [60.9 -19.3] :se [56.4 -15.7] :so :v}
           (sut/find-by-id "AL3"))))
  (testing "partial squares"
    (is (= {:id "AL5" :nw [56.4 -23.5] :se [53.7 -20.5] :sub [[1 2] [4 5] [7 8]]}
           (sut/find-by-id "AL5")))))


