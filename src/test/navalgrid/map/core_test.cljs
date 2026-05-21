(ns navalgrid.map.core-test
  (:require [cljs.test :refer [deftest is testing]])
  (:require [navalgrid.map.core :as sut]))

(deftest coord<->lngLat-test
  (is (= [33 99] (sut/coord<->lngLat [99 33]))))

(deftest bounds-test
  (testing "single square"
    (is (= [[48.3 -11.5] [51 -7]]
           (sut/bounds [{:nw [51 -11.5] :se [48.3 -7]}]))
        "for the rectangle BF1")
    (is (= [[42.9 -11.5] [51 3.5]]
           (sut/bounds [{:poly [[51 -11.5] [51 3.5] [50.1 3.5] [50.1 2] [49.2 2] [49.2 0.5] [48.3 0.5] [48.3 -1] [45.6 -1] [45.6 -0.7] [42.9 -0.7] [42.9 -11.5]]}]))
        "for the polygon BF"))
  (testing "multiple squares"
    (is (= [[42.9 -11.5] [51 -0.7]]
           (sut/bounds [{:nw [51, -11.5] :se [48.3, -7]} {:nw [45.6, -4.3] :se [42.9, -0.7]}]))
        "for rectangular BF1 and BF9")
    (is (= [[42.9 -11.5] [51 11]]
           (sut/bounds [{:poly [[51 -11.5] [51 3.5] [50.1 3.5] [50.1 2] [49.2 2] [49.2 0.5] [48.3 0.5] [48.3 -1] [45.6 -1] [45.6 -0.7] [42.9 -0.7] [42.9 -11.5]]}
                          {:poly [[51 3.5] [51 6.5] [47.4 6.5] [47.4 11] [45.6 11] [45.6 10.1] [44.7 10.1] [44.7 6.5] [43.8 6.5] [43.8 2.9] [42.9 2.9] [42.9 -0.7] [45.6 -0.7] [45.6 -1] [48.3 -1] [48.3 0.5] [49.2 0.5] [49.2 2] [50.1 2] [50.1 3.5]]}]))
        "for polygonal BF and BG")))

(deftest fix-for-antimeridian-test
  (testing "Coordinates on either side of the antimeridian are 'fixed' for maplibre"
    (is
      (= [[-160 90] [-190 90]]
         (sut/fix-for-antimeridian [[-160 90] [170 90]]))
      "with two values")
    (is
      (= [[-160 90] [-190 90] [-160 90] [-170 90]]
         (sut/fix-for-antimeridian [[-160 90] [170 90] [-160 90] [-170 90]]))
      "with multiple negative values")
    (is
      (= [[160 90] [190 90] [160 90] [170 90]]
         (sut/fix-for-antimeridian [[160 90] [-170 90] [160 90] [170 90]]))
      "with multiple positive values")))

(deftest square->polygon-test
  (is (= [[-11.5 51] [-7 51] [-7 48.3] [-11.5 48.3] [-11.5 51]]
         (sut/square->polygon {:nw [51 -11.5] :se [48.3 -7]}))
      "for the rectangular square BF1")
  (is (= [[-11.5 51] [3.5 51] [3.5 50.1] [2 50.1] [2 49.2] [0.5 49.2] [0.5 48.3] [-1 48.3] [-1 45.6] [-0.7 45.6] [-0.7 42.9] [-11.5 42.9] [-11.5 51]]
         (sut/square->polygon {:poly [[51 -11.5] [51 3.5] [50.1 3.5] [50.1 2] [49.2 2] [49.2 0.5] [48.3 0.5] [48.3 -1] [45.6 -1] [45.6 -0.7] [42.9 -0.7] [42.9 -11.5]]}))
      "for the polygonal square BF"))

(deftest polygon->geojson-test
  (is (= (sut/polygon->geojson [[-11.5 51] [-7 51] [-7 48.3] [-11.5 48.3] [-11.5 51]])
         {:type "geojson"
          :data {:type     "Feature"
                 :geometry {:type "Polygon" :coordinates [[[-11.5 51] [-7 51] [-7 48.3] [-11.5 48.3] [-11.5 51]]]} :properties {}}})))

(deftest polygons->geojson-test
  (is (= (sut/polygons->geojson [[[160 90] [190 90] [160 90] [170 90]] [[-11.5 51] [-7 51] [-7 48.3] [-11.5 48.3] [-11.5 51]]])
         {:type "geojson",
          :data {:type     "FeatureCollection",
                 :features [{:type "Feature", :geometry {:type "Polygon", :coordinates [[[160 90] [190 90] [160 90] [170 90]]]}, :properties {}}
                            {:type "Feature", :geometry {:type "Polygon", :coordinates [[[-11.5 51] [-7 51] [-7 48.3] [-11.5 48.3] [-11.5 51]]]}, :properties {}}]}})))
