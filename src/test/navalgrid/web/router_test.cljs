(ns navalgrid.web.router-test
  (:require [clojure.test :refer [deftest is testing]])
  (:require [navalgrid.web.router :as sut]))

(deftest path->segments-test
  (is (= ["foo" "bar"] (sut/path->segments "/foo/bar")) "splits path by /")
  (is (= ["foo/bar"] (sut/path->segments "/foo%2Fbar")) "decodes URI component")
  (is (= [] (sut/path->segments "//")) "ignores empty segments")
  (is (= [] (sut/path->segments "")) "ignores empty path"))

(deftest segments->path-test
  (is (= "/foo/bar" (sut/segments->path ["foo" "bar"])) "joins segments to path")
  (is (= "/f%20o/b%2Fr" (sut/segments->path ["f o" "b/r"])) "encodes URI component")
  (is (= "/" (sut/segments->path ["" ""])) "ignores empty components")
  (is (= "/" (sut/segments->path [])) "ignores empty segments"))
