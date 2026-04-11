(ns navalgrid.web.clipboard)

(defn copy-for-old-browsers! [s]
  (let [ta (doto (.createElement js/document "textarea")
             (aset "value" s)
             (aset "style" "position:fixed; left:-9999px; top:0;"))]
    (.appendChild (.-body js/document) ta)
    (.select ta)
    (let [ok (try
               (.execCommand js/document "copy")
               true
               (catch :default e
                 (js/console.error "execCommand copy failed" e)
                 false))]
      (.removeChild (.-body js/document) ta)
      ok)))

(defn copy! [s]
  (if (and js/navigator (.-clipboard js/navigator) (.-writeText (.-clipboard js/navigator)))
    (.writeText (.-clipboard js/navigator) s)
    (js/Promise. (fn [resolve reject]
                   (try
                     (if (copy-for-old-browsers! s)
                       (resolve true)
                       (reject (js/Error. "copy failed")))
                     (catch :default e
                       (reject e)))))))