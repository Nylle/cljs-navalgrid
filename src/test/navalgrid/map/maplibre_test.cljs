(ns navalgrid.map.maplibre-test
  (:require [goog.object :as gobj]
            ["maplibre-gl" :as maplibregl]
            [cljs.test :refer [deftest is testing async]]
            [navalgrid.map.maplibre :as sut]))

(deftest create!-test
  (let [mock-map (js-obj)
        handlers (atom {})
        calls (atom [])]
    (aset mock-map "addControl" (fn [c] (swap! calls conj [:add-control c]) mock-map))
    (aset mock-map "on" (fn [event handler]
                          (swap! calls conj [:on event handler])
                          (swap! handlers update event (fnil conj []) handler)
                          mock-map))
    (reset! sut/map-inst nil)

    (let [captured-props (atom {})
          constructor-bak (.-Map maplibregl)]
      (try
        (set! (.-Map maplibregl) (fn [props] (reset! captured-props props) mock-map))

        (let [ref (atom (js/document.createElement "div"))
              loaded-fn (fn [] ())
              moved-fn (fn [] ())
              props {:some "prop"}]

          (sut/create! ref props loaded-fn moved-fn)

          (testing "props are passed to Map"
            (let [actual (js->clj @captured-props :keywordize-keys true)
                  expected (assoc {:some "prop"} :container @ref)]
              (is (= expected actual))))

          (testing "on-load event-handler is being registered"
            (is (some #(and (= :on (first %))
                            (= "load" (second %))
                            (identical? loaded-fn (nth % 2))) @calls)))

          (testing "on-moveend event-handler is being registered"
            (is (some #(and (= :on (first %))
                            (= "moveend" (second %))
                            (identical? moved-fn (nth % 2))) @calls)))

          (testing "map-inst is set to created map"
            (is (identical? mock-map @sut/map-inst))))

        (finally
          (set! (.-Map maplibregl) constructor-bak))))))

(deftest destroy!-test
  (let [mock-map (js-obj)
        removed (atom false)]
    (aset mock-map "remove" (fn [] (reset! removed true) nil))
    (reset! sut/map-inst mock-map)

    (sut/destroy!)

    (testing "calls .remove on map-inst"
      (is @removed))

    (testing "resets map-inst to nil"
      (is (nil? @sut/map-inst)))))

(deftest get-center-test
  (let [mock-map (js-obj)
        mock-center (js-obj)]
    (aset mock-center "lng" 12.34)
    (aset mock-center "lat" 56.78)
    (aset mock-map "getCenter" (fn [] mock-center))

    (testing "returns center"
      (reset! sut/map-inst mock-map)
      (is (= [12.34 56.78] (sut/get-center))))

    (testing "returns nil when map-inst is nil"
      (reset! sut/map-inst nil)
      (is (nil? (sut/get-center))))))

(deftest set-center!-test
  (let [mock-map (js-obj)
        called (atom nil)]
    (aset mock-map "setCenter" (fn [c] (reset! called c) nil))

    (testing "sets center in map-inst"
      (reset! sut/map-inst mock-map)
      (sut/set-center! [1.23 4.56])

      (is (some? @called))
      (is (= 1.23 (aget @called 0)))
      (is (= 4.56 (aget @called 1)))
      (is (identical? mock-map @sut/map-inst)))

    (testing "does nothing when map-inst is nil"
      (reset! sut/map-inst nil)
      (reset! called nil)
      (sut/set-center! [7 8])

      (is (nil? @called)))))

(deftest get-zoom-test
  (let [mock-map (js-obj)]
    (aset mock-map "getZoom" (fn [] 8.5))

    (testing "returns zoom"
      (reset! sut/map-inst mock-map)
      (is (= 8.5 (sut/get-zoom))))

    (testing "returns nil when map-inst is nil"
      (reset! sut/map-inst nil)
      (is (nil? (sut/get-zoom))))))

(deftest set-zoom!-test
  (let [mock-map (js-obj)
        called (atom nil)]
    (aset mock-map "setZoom" (fn [z] (reset! called z) nil))

    (testing "sets zoom in map-inst"
      (reset! sut/map-inst mock-map)
      (sut/set-zoom! 4.25)

      (is (= 4.25 @called))
      (is (identical? mock-map @sut/map-inst)))

    (testing "does nothing when map-inst is nil"
      (reset! sut/map-inst nil)
      (reset! called nil)
      (sut/set-zoom! 9)

      (is (nil? @called)))))

(deftest add-source!-test
  (let [mock-map (js-obj)
        called (atom nil)]
    (aset mock-map "addSource" (fn [id arg] (reset! called [id arg]) nil))

    (testing "adds specified source to map-inst"
      (reset! sut/map-inst mock-map)
      (sut/add-source! "src-1" {:type "geojson" :data {:type "FeatureCollection" :features []}})

      (is (identical? mock-map @sut/map-inst))
      (is (= "src-1" (first @called)))
      (let [js-arg (second @called)]
        (is (= "geojson" (.-type js-arg)))
        (is (= "FeatureCollection" (.. js-arg -data -type)))))

    (testing "does nothing when map-inst is nill"
      (reset! sut/map-inst nil)
      (reset! called nil)
      (sut/add-source! "src-2" {:type "geojson"})

      (is (nil? @called)))))

(deftest remove-source!-test
  (let [mock-map (js-obj)
        removed (atom nil)
        get-called (atom nil)]
    (aset mock-map "getSource" (fn [id] (reset! get-called id) (when (= id "exists") (js-obj))))
    (aset mock-map "removeSource" (fn [id] (reset! removed id) nil))

    (testing "removes source with specified id from map-inst"
      (reset! sut/map-inst mock-map)
      (sut/remove-source! "exists")

      (is (= "exists" @get-called))
      (is (= "exists" @removed))
      (is (identical? mock-map @sut/map-inst)))

    (testing "does nothing when source does not exist on map-inst"
      (reset! get-called nil)
      (reset! removed nil)
      (sut/remove-source! "missing")

      (is (= "missing" @get-called))
      (is (nil? @removed)))

    (testing "does nothing when map-inst is nil"
      (reset! sut/map-inst nil)
      (reset! get-called nil)
      (reset! removed nil)
      (sut/remove-source! "exists")

      (is (nil? @get-called))
      (is (nil? @removed)))))

(deftest add-layer!-test
  (let [mock-map (js-obj)
        called (atom nil)]
    (aset mock-map "addLayer" (fn [arg] (reset! called arg) nil))

    (testing "adds specified layer to map-inst"
      (reset! sut/map-inst mock-map)
      (sut/add-layer! {:id "layer-1" :type "fill" :source "src-1"})

      (is (some? @called))
      (is (= "layer-1" (.-id @called)))
      (is (= "fill" (.-type @called)))
      (is (= "src-1" (.-source @called)))
      (is (identical? mock-map @sut/map-inst)))

    (testing "does nothing when map-inst is nil"
      (reset! sut/map-inst nil)
      (reset! called nil)
      (sut/add-layer! {:id "layer-2"})

      (is (nil? @called)))))

(deftest remove-layer!-test
  (let [mock-map (js-obj)
        removed (atom nil)
        get-called (atom nil)]
    (aset mock-map "getLayer" (fn [id] (reset! get-called id) (when (= id "exists") (js-obj))))
    (aset mock-map "removeLayer" (fn [id] (reset! removed id) nil))

    (testing "removes layer with specified id from map-inst"
      (reset! sut/map-inst mock-map)
      (sut/remove-layer! "exists")

      (is (= "exists" @get-called))
      (is (= "exists" @removed))
      (is (identical? mock-map @sut/map-inst)))

    (testing "does nothing when layer does not exist"
      (reset! get-called nil)
      (reset! removed nil)
      (sut/remove-layer! "missing")

      (is (= "missing" @get-called))
      (is (nil? @removed)))

    (testing "does nothing when map-inst is nil"
      (reset! sut/map-inst nil)
      (reset! get-called nil)
      (reset! removed nil)
      (sut/remove-layer! "exists")

      (is (nil? @get-called))
      (is (nil? @removed)))))

(deftest hide-layer!-test
  (let [mock-map (js-obj)
        get-layer-calls (atom [])
        set-layout-calls (atom [])]
      (aset mock-map "getLayer" (fn [id] (swap! get-layer-calls conj id) (when (= id "exists") #js {})))
      (aset mock-map "setLayoutProperty" (fn [id prop val] (swap! set-layout-calls conj [id prop val])))

      (testing "sets visibility of existing layer to none"
        (reset! get-layer-calls [])
        (reset! set-layout-calls [])
        (reset! sut/map-inst mock-map)
        (sut/hide-layer! "exists")

        (is (= ["exists"] @get-layer-calls))
        (is (= [["exists" "visibility" "none"]] @set-layout-calls))
        (is (identical? mock-map @sut/map-inst)))

      (testing "does nothing when layer does not exist"
        (reset! get-layer-calls [])
        (reset! set-layout-calls [])
        (reset! sut/map-inst mock-map)
        (sut/hide-layer! "missing")

        (is (= ["missing"] @get-layer-calls))
        (is (empty? @set-layout-calls))
        (is (identical? mock-map @sut/map-inst)))))

(deftest show-layer!-test
  (let [mock-map (js-obj)
        get-layer-calls (atom [])
        set-layout-calls (atom [])]
      (aset mock-map "getLayer" (fn [id] (swap! get-layer-calls conj id) (when (= id "exists") #js {})))
      (aset mock-map "setLayoutProperty" (fn [id prop val] (swap! set-layout-calls conj [id prop val])))

      (testing "sets visibility if existing layer to visible"
        (reset! get-layer-calls [])
        (reset! set-layout-calls [])
        (reset! sut/map-inst mock-map)
        (sut/show-layer! "exists")

        (is (= ["exists"] @get-layer-calls))
        (is (= [["exists" "visibility" "visible"]] @set-layout-calls))
        (is (identical? mock-map @sut/map-inst)))

      (testing "does nothing when layer does not exist"
        (reset! get-layer-calls [])
        (reset! set-layout-calls [])
        (reset! sut/map-inst mock-map)
        (sut/show-layer! "missing")

        (is (= ["missing"] @get-layer-calls))
        (is (empty? @set-layout-calls))
        (is (identical? mock-map @sut/map-inst)))))

(deftest fit-bounds!-test
  (let [mock-map (js-obj)
        called (atom nil)]
    (aset mock-map "fitBounds" (fn [bounds opts] (reset! called [bounds opts]) nil))

    (testing "calls .fitBounds on map-inst"
      (reset! sut/map-inst mock-map)
      (sut/fit-bounds! [[-10 20] [30 40]])

      (is (identical? mock-map @sut/map-inst))
      (is (some? @called))
      (let [[js-bounds js-opts] @called]
        (is (= -10 (aget js-bounds 0 0)))
        (is (= 20 (aget js-bounds 0 1)))
        (is (= 30 (aget js-bounds 1 0)))
        (is (= 40 (aget js-bounds 1 1)))
        (is (= 50 (.-padding js-opts)))))

    (testing "does nothing when map-inst is nil"
      (reset! sut/map-inst nil)
      (reset! called nil)
      (sut/fit-bounds! [[0 0] [1 1]])

      (is (nil? @called)))))

(deftest create-marker-test
  (let [mock-marker (js-obj)
        captured-opts (atom nil)
        captured-lnglat (atom nil)
        calls (atom [])]
    (aset mock-marker "setLngLat" (fn [lnglat] (swap! calls conj [:set-lnglat lnglat]) (reset! captured-lnglat lnglat) mock-marker))

    (let [constructor-bak (.-Marker maplibregl)]
      (try
        (set! (.-Marker maplibregl) (fn [opts] (reset! captured-opts opts) mock-marker))

        (let [text "hello"
              lnglat [12.34 56.78]
              class "my-css-class"
              actual (sut/create-marker text lnglat class nil)]

          (testing "returns new marker"
            (is (identical? mock-marker actual)))

          (testing "Marker constructor was called with expected div-element"
            (is (= "div" (.toLowerCase (.-tagName (.-element @captured-opts)))))
            (is (= class (.-className (.-element @captured-opts))))
            (is (= text (.-textContent (.-element @captured-opts)))))

          (testing ".setLngLat was called with provided coordinates"
            (is (= (js->clj @captured-lnglat) (js->clj (clj->js lnglat))))))

        (finally
          (set! (.-Marker maplibregl) constructor-bak))))))

(deftest add-marker!-test
  (let [mock-map (js-obj)
        mock-marker (js-obj)
        add-to-calls (atom [])
        markers-bak @sut/markers
        map-inst-bak @sut/map-inst]
    (try
      (aset mock-marker "addTo" (fn [m] (swap! add-to-calls conj m) mock-marker))
      (reset! sut/map-inst mock-map)
      (reset! sut/markers {})

      (let [id "marker-1"
            actual (sut/add-marker! id mock-marker)]

        (testing "returns the map-inst"
          (is (identical? mock-map actual)))

        (testing ".addTo is called with current map-inst"
          (is (= [mock-map] @add-to-calls)))

        (testing "markers atom is updated with the marker under id"
          (is (identical? mock-marker (get @sut/markers id)))))

      (finally
        (reset! sut/markers markers-bak)
        (reset! sut/map-inst map-inst-bak)))))

(deftest clear-markers!-test
  (let [removed (atom [])
        m1 (js-obj)
        m2 (js-obj)
        markers-bak @sut/markers]
    (try
      (aset m1 "remove" (fn [] (swap! removed conj :m1)))
      (aset m2 "remove" (fn [] (swap! removed conj :m2)))

      (reset! sut/markers {"a" m1 "b" m2})

      (sut/clear-markers!)

      (testing "all markers in atom are being removed"
        (is (= #{:m1 :m2} (set @removed)))
        (is (empty? @sut/markers)))

      (finally
        (reset! sut/markers markers-bak)))))

(deftest meters-per-pixel-test
  (is (= 19567.879241210936 (sut/meters-per-pixel 0 3))))

(deftest get-scale-denominator-test
  (is (= 73957338.8644193 (sut/get-scale-denominator 0 3))))
