(ns navalgrid.domain.geo-test
  (:require [cljs.test :refer [deftest is testing]]
            [cljs.math :as math]
            [navalgrid.domain.geo :as geo]))

(deftest deg->rad-test
  (is (= (geo/deg->rad 0) 0))
  (is (= (geo/deg->rad 180) 3.141592653589793))
  (is (= (geo/deg->rad 360) 6.283185307179586)))

(deftest rhumb-distance-test
  (let [sut geo/rhumb-distance]
    (testing "Nautical miles on the equator"
      (is (= 120 (math/round (sut {:lat 0 :lon 1} {:lat 0 :lon -1}))))
      (is (= 240 (math/round (sut {:lat 0 :lon 2} {:lat 0 :lon -2}))))
      (is (= 360 (math/round (sut {:lat 0 :lon 3} {:lat 0 :lon -3}))))
      (is (= 480 (math/round (sut {:lat 0 :lon 4} {:lat 0 :lon -4}))))
      (is (= 600 (math/round (sut {:lat 0 :lon 5} {:lat 0 :lon -5}))))
      (is (= 120 (math/round (sut {:lat 0 :lon -1} {:lat 0 :lon 1}))))
      (is (= 240 (math/round (sut {:lat 0 :lon -2} {:lat 0 :lon 2}))))
      (is (= 360 (math/round (sut {:lat 0 :lon -3} {:lat 0 :lon 3}))))
      (is (= 480 (math/round (sut {:lat 0 :lon -4} {:lat 0 :lon 4}))))
      (is (= 600 (math/round (sut {:lat 0 :lon -5} {:lat 0 :lon 5})))))))

(deftest simple-rhumb-division-test
  (let [sut geo/simple-rhumb-division]
    (testing "Invalid bearing"
      (is (= '() (sut {:lat 0 :lon 3} {:lat 0 :lon 3} 3)))
      (is (= '() (sut {:lat 3 :lon 0} {:lat 3 :lon 0} 3)))
      (is (thrown-with-msg? :default
                            #"Invalid bearing from \{\:lat 1, \:lon 2} to \{\:lat 3, \:lon 4}\. Must be one of 0°, 90°, 180°, 270°\."
                            (sut {:lat 1 :lon 2} {:lat 3 :lon 4} 1))))
    (testing "Regular case"
      (is (= '({:lat 0 :lon 3} {:lat 0 :lon 4} {:lat 0 :lon 5}) (sut {:lat 0 :lon 3} {:lat 0 :lon 6} 3)))
      (is (= '({:lat 3 :lon 0} {:lat 4 :lon 0} {:lat 5 :lon 0}) (sut {:lat 3 :lon 0} {:lat 6 :lon 0} 3))))
    (testing "Latitude across the equator"
      (is (= '({:lat 3 :lon 0} {:lat 1 :lon 0} {:lat -1 :lon 0}) (sut {:lat 3 :lon 0} {:lat -3 :lon 0} 3)))
      (is (= '({:lat -3 :lon 0} {:lat -1 :lon 0} {:lat 1 :lon 0}) (sut {:lat -3 :lon 0} {:lat 3 :lon 0} 3))))
    (testing "Longitude across the meridian"
      (is (= '({:lat 0 :lon -3} {:lat 0 :lon -1} {:lat 0 :lon 1}) (sut {:lat 0 :lon -3} {:lat 0 :lon 3} 3)))
      (is (= '({:lat 0 :lon 3} {:lat 0 :lon 1} {:lat 0 :lon -1}) (sut {:lat 0 :lon 3} {:lat 0 :lon -3} 3))))
    (testing "Longitude across the anti-meridian"
      (is (= '({:lat 0 :lon 165} {:lat 0 :lon 175} {:lat 0 :lon -175}) (sut {:lat 0 :lon 165} {:lat 0 :lon -165} 3)))
      (is (= '({:lat 0 :lon -165} {:lat 0 :lon -175} {:lat 0 :lon 175}) (sut {:lat 0 :lon -165} {:lat 0 :lon 165} 3))))))


