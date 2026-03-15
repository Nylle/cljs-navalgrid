(ns navalgrid.persistence.repository-test
  (:require [cljs.test :refer [deftest is testing]]
            [navalgrid.persistence.repository :as sut]))

(deftest two-by-five-search-key-test
  (is (= "AK1" (sut/two-by-five-search-key "AK1")))
  (is (= "AK1" (sut/two-by-five-search-key "AK01"))))

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
    (is (= nil (sut/find-by-id {:l "xxx"}))))
  (testing "large regular square"
    (is (= (sut/find-by-id "ÄG") {:id "ÄG" :nw [85.2 5] :se [77.1 45.5]}))
    (is (= (sut/find-by-id "ÄG1") {:id "ÄG1" :nw [85.2 5] :se [82.5 18.5]}))
    (is (= (sut/find-by-id "ÄG9999") {:id "ÄG9999" :nw [77.2 45] :se [77.1 45.5]})))
  (testing "large partial square OT with columns [[1] [4] [7]]"
    (is (= (sut/find-by-id "OT") {:id "OT" :nw [33.8 167] :se [25.7 170.6]}))
    (is (= (sut/find-by-id "OT1") {:id "OT1" :nw [33.8 167] :se [31.1 170.6]}))
    (is (= (sut/find-by-id "OT2") nil))
    (is (= (sut/find-by-id "OT7999") {:id "OT7999" :nw [25.8 170.467] :se [25.7 170.6]})))
  (testing "large partial square YC with row [7 8 9]"
    (is (= (sut/find-by-id "YC") {:id "YC" :nw [85.1 126.5] :se [82.4 -172.75]}))
    (is (= (sut/find-by-id "YC1") nil))
    (is (= (sut/find-by-id "YC7") {:id "YC7" :nw [85.1 126.5] :se [82.4 146.75]}))
    (is (= (sut/find-by-id "YC9999") {:id "YC9999" :nw [82.5 -173.5] :se [82.4 -172.75]})))
  (testing "irregular squares"
    (is (= (sut/find-by-id "ÄA") {:id "ÄA" :nw [60.9 -71.5] :se [59.1 -44.5]}))
    (is (= (sut/find-by-id "MA48") {:id "MA48" :nw [29.3 38.9] :se [28.4 39.8]})))
  (testing "polygonal squares"
    (is (= (sut/find-by-id "AM6") {:id "AM6" :poly [[56.4 -7] [56.4 -4] [55.5 -4] [55.5 -2.5] [53.7 -2.5] [53.7 -7]]}))
    (is (= (sut/find-by-id "XN") {:id "XN" :poly [[82.4 -103] [82.4 -76] [77.1 -76] [77.1 -85] [74.3 -85] [74.3 -103]]})))
  (testing "two-by-five squares"
    (is (= (sut/find-by-id "AK1") {:id "AK1" :nw [60.9 -37.3] :se [56.4 -33.7] :so :v}))
    (is (= (sut/find-by-id "AK11") {:id "AK11" :nw [60.9 -37.3] :se [60 -35.5]}))
    (is (= (sut/find-by-id "AK115") {:id "AK115" :nw [60.6 -36.7] :se [60.3 -36.1]}))
    (is (= (sut/find-by-id "AK18") {:id "AK18" :nw [58.2 -35.5] :se [57.3 -33.7]}))
    (is (= (sut/find-by-id "AK01") {:id "AK01" :nw [57.3 -35.5] :se [56.4 -33.7]}))
    (is (= (sut/find-by-id "AK0199") {:id "AK0199" :nw [56.5 -33.9] :se [56.4 -33.7]})))
  (testing "partial squares [[1 2] [4 5] [7 8]]"
    (is (= (sut/find-by-id "AL5") {:id "AL5" :nw [56.4 -23.5] :se [53.7 -20.5]}))
    (is (= (sut/find-by-id "AL51") {:id "AL51" :nw [56.4 -23.5] :se [55.5 -22]}))
    (is (= (sut/find-by-id "AL53") nil))
    (is (= (sut/find-by-id "AL5899") {:id "AL5899" :nw [53.8 -20.667] :se [53.7 -20.5]}))
    (is (= (sut/find-by-id "AL59") nil))
    (is (= (sut/find-by-id "AL599") nil))
    (is (= (sut/find-by-id "AL5999") nil)))
  (testing "combination of polygonal, irregular, and partial squares"
    (is (= (sut/find-by-id "AD") {:id "AD" :poly [[69 -37.75] [69 -24.25] [60.9 -24.25] [60.9 -37.3] [59.1 -37.3] [59.1 -44.5] [66.3 -44.5] [66.3 -37.75]]}) "polygonal")
    (is (= (sut/find-by-id "AD1") {:id "AD1" :nw [69 -37.75] :se [66.3 -31]}) "partial")
    (is (= (sut/find-by-id "AD4") {:id "AD4" :nw [66.3 -37.75] :se [63.6 -31]}) "partial")
    (is (= (sut/find-by-id "AD8") {:id "AD8" :nw [63.6 -31] :se [60.9 -24.25]}) "partial")
    (is (= (sut/find-by-id "AD9") {:id "AD9" :nw [60.9 -44.5] :se [59.1 -37.3]}) "irregular")
    (is (= (sut/find-by-id "AD91") {:id "AD91" :nw [60.9 -44.5] :se [60 -42.7]}) "partial")
    (is (= (sut/find-by-id "AD98") {:id "AD98" :nw [60 -39.1] :se [59.1 -37.3]}) "partial")
    (is (= (sut/find-by-id "AD99") nil) "does not exist")))
