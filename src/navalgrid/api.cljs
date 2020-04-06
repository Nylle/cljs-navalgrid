(ns navalgrid.api
  (:require [cljs-http.client :as http]))

(defn get-square [id]
  (http/get (str "http://localhost:54309/rest/square/" (clojure.string/lower-case id)) {:with-credentials? false}))
