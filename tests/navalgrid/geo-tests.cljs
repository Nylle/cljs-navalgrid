(ns ^:figwheel-always navalgrid.geo-tests
  (:require [navalgrid.geo :as sut]
            [cljs.test :refer-macros [deftest is testing run-tests]]))

(deftest test--polygon
  (testing "For a GeoJSON polygon"
           (is
            (= [[1 1] [2 2] [3 3] [1 1]]
               (sut/polygon [[1 1] [2 2] [3 3]]))
            "the first coordinate is appended to 'close' the polygon")
           (is
            (= [[-160 90] [-190 90] [-160 90]]
               (sut/polygon [[-160 90] [170 90]]))
            "the fix for crossing the anti-meridian in mapbox is applied")))

(deftest test--fix-for-antimeridian
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
