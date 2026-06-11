(ns navalgrid.web.download)

(defn download-fn [body-fn filename]
  (fn [e]
    (.preventDefault e)
    (let [blob (js/Blob. (array (body-fn)) #js {:type "application/xml;charset=utf-8"})
          url  (js/URL.createObjectURL blob)
          a    (.createElement js/document "a")]
      (aset a "href" url)
      (aset a "download" filename)
      (.appendChild (.-body js/document) a)
      (.click a)
      (.removeChild (.-body js/document) a)
      (js/setTimeout #(js/URL.revokeObjectURL url) 1500))))