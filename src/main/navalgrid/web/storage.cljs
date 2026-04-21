(ns navalgrid.web.storage)

(defn ls-available? []
  (try
    (let [k "__test__"]
      (.setItem js/localStorage k "1")
      (.removeItem js/localStorage k)
      true)
    (catch :default _ false)))

(defn ls-set! [k v]
  (when (ls-available?)
    (cljs.pprint/pprint [k v])
    (.setItem js/localStorage (name k) (js/JSON.stringify (clj->js v)))))

(defn ls-get [k]
  (when (ls-available?)
    (when-let [s (.getItem js/localStorage (name k))]
      (js->clj (js/JSON.parse s) :keywordize-keys true))))

(defn ls-remove! [k]
  (when (ls-available?) (.removeItem js/localStorage (name k))))
