(ns ^:figwheel-always navalgrid.geo-tests
  (:require [navalgrid.geo :as sut]
            [cljs.test :refer-macros [deftest is testing run-tests]]))

(deftest test-lnglat
  (testing "A coordinate object is converted to GeoJSON"
           (is (= [0 9] (sut/lnglat {:Latitude 9 :Longitude 0})))))

(deftest test-polygon
  (testing "For a GeoJSON polygon"
           (is
            (= [[1 1] [2 2] [3 3] [1 1]]
               (sut/polygon
                [{:Latitude 1 :Longitude 1}
                 {:Latitude 2 :Longitude 2}
                 {:Latitude 3 :Longitude 3}]))
            "coordinates are mapped to lnglat vectors and the first coordinate is appended to 'close' the polygon")))

(deftest test-fix-for-antimeridian
  (testing "Coordinates on either side of the antimeridian are 'fixed' for mapbox"
           (is
            (= [[-160 90] [-190 90]]
               (sut/fix-for-antimeridian
                [[-160 90] [170 90]]))
            "with two values")
           (is
            (= [[-160 90] [-190 90] [-160 90] [-170 90]]
               (sut/fix-for-antimeridian
                [[-160 90] [170 90] [-160 90] [-170 90]]))
            "with multiple negative values")
           (is
            (= [[160 90] [190 90] [160 90] [170 90]]
               (sut/fix-for-antimeridian
                [[160 90] [-170 90] [160 90] [170 90]]))
            "with multiple positive values")))

(cljs.test/run-tests)