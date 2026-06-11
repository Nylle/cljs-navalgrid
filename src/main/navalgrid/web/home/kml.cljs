(ns navalgrid.web.home.kml
  (:require [navalgrid.map.core :as m]
            [clojure.string :as str]))

(defn escape-str [s]
  (-> s
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")))

(defn attrs->str [attrs]
  (when (seq attrs)
    (str " " (str/join " " (for [[k v] attrs] (str (name k) "=\"" (escape-str (str v)) "\""))))))

(defn node->xml [node]
  (cond
    (nil? node) ""
    (string? node) (escape-str node)
    (vector? node) (let [[tag & rest] node
                         [maybe-attrs children] (if (and (map? (first rest))) [(first rest) (next rest)] [nil rest])
                         tag-name (name tag)
                         inner (apply str (map node->xml children))]
                     (if (and (empty? inner) (not (some? maybe-attrs)))
                       (str "<" tag-name (attrs->str maybe-attrs) "/>")
                       (str "<" tag-name (attrs->str maybe-attrs) ">" inner "</" tag-name ">")))
    (seqable? node) (apply str (map node->xml node))
    :else (escape-str (str node))))

(defn doc [square]
  (let [id (:id square)
        c (:center square)
        center (str (second c) "," (first c))
        ring (str/join " " (map #(str (first %) "," (second %)) (m/square->polygon square)))]
    [:Document
     [:Style {:id "default"}
      [:LineStyle
       [:color "C80000ff"]
       [:width 4]]
      [:IconStyle
       [:color "C8ffffff"]
       [:scale 1]
       [:Icon [:href "http://maps.google.com/mapfiles/kml/shapes/placemark_square.png"]]
       [:hotSpot {:x 0 :y 0 :xunits "fraction" :yunits "fraction"}]]]
     [:name id]
     [:description "Exported from navalgrid.com"]
     [:Placemark
      [:name id]
      [:description]
      [:styleUrl "#default"]
      [:Point
       [:coordinates center]]]
     [:Placemark
      [:name]
      [:description]
      [:styleUrl "#default"]
      [:LineString
       [:tessellate 1]
       [:coordinates ring]]]]))

(defn square->kml [square]
  (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" (node->xml (doc square))))