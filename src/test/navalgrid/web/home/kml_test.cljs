(ns navalgrid.web.home.kml-test
  (:require [clojure.test :refer [deftest is testing]])
  (:require [navalgrid.web.home.kml :as sut]))

(deftest escape-str-test
  (is (= "&amp;" (sut/escape-str "&")))
  (is (= "&lt;" (sut/escape-str "<")))
  (is (= "&gt;" (sut/escape-str ">")))
  (is (= "&quot;" (sut/escape-str "\""))))

(deftest attrs->str-test
  (is (= " first=\"1\" second=\"2\"" (sut/attrs->str {:first "1" :second "2"}))))

(deftest node->xml-test
  (is (= "<tag/>" (sut/node->xml [:tag])))
  (is (= "<tag>hello</tag>" (sut/node->xml [:tag "hello"])))
  (is (= "<tag>he&lt;ll&gt;o</tag>" (sut/node->xml [:tag "he<ll>o"])))
  (is (= "<tags><tag>1</tag><tag>2</tag></tags>" (sut/node->xml [:tags [:tag 1] [:tag 2]])))
  (is (= "<note to=\"Alice\">Hi<br/><from>Bob</from></note>" (sut/node->xml [:note {:to "Alice"} "Hi" [:br] [:from "Bob"]]))))

(deftest doc-test
  (is (= [:Document
          [:Style {:id "default"} [:LineStyle [:color "C80000ff"] [:width 4]] [:IconStyle [:color "C8ffffff"] [:scale 1] [:Icon [:href "http://maps.google.com/mapfiles/kml/shapes/placemark_square.png"]] [:hotSpot {:x 0, :y 0, :xunits "fraction", :yunits "fraction"}]]]
          [:name "BF"] [:description "Exported from navalgrid.com"]
          [:Placemark [:name "BF"] [:description] [:styleUrl "#default"] [:Point [:coordinates "-4,46.95"]]]
          [:Placemark [:name] [:description] [:styleUrl "#default"] [:LineString [:tessellate 1] [:coordinates "-11.5,51 3.5,51 3.5,50.1 2,50.1 2,49.2 0.5,49.2 0.5,48.3 -1,48.3 -1,45.6 -0.7,45.6 -0.7,42.9 -11.5,42.9 -11.5,51"]]]]
         (sut/doc {:id     "BF"
                   :poly   [[51 -11.5] [51 3.5] [50.1 3.5] [50.1 2] [49.2 2] [49.2 0.5] [48.3 0.5] [48.3 -1] [45.6 -1] [45.6 -0.7] [42.9 -0.7] [42.9 -11.5]]
                   :center [46.95 -4]})))
  (is (= [:Document
          [:Style {:id "default"} [:LineStyle [:color "C80000ff"] [:width 4]] [:IconStyle [:color "C8ffffff"] [:scale 1] [:Icon [:href "http://maps.google.com/mapfiles/kml/shapes/placemark_square.png"]] [:hotSpot {:x 0, :y 0, :xunits "fraction", :yunits "fraction"}]]]
          [:name "BF4664"] [:description "Exported from navalgrid.com"]
          [:Placemark [:name "BF4664"] [:description] [:styleUrl "#default"] [:Point [:coordinates "-7.41666,46.95"]]]
          [:Placemark [:name] [:description] [:styleUrl "#default"] [:LineString [:tessellate 1] [:coordinates "-7.5,47 -7.33333,47 -7.33333,46.9 -7.5,46.9 -7.5,47"]]]]
         (sut/doc {:id     "BF4664"
                   :nw     [47 -7.5]
                   :se     [46.9 -7.33333]
                   :center [46.95 -7.41666]}))))

(deftest square->kml-test
  (is (= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Document><Style id=\"default\"><LineStyle><color>C80000ff</color><width>4</width></LineStyle><IconStyle><color>C8ffffff</color><scale>1</scale><Icon><href>http://maps.google.com/mapfiles/kml/shapes/placemark_square.png</href></Icon><hotSpot x=\"0\" y=\"0\" xunits=\"fraction\" yunits=\"fraction\"></hotSpot></IconStyle></Style><name>BF4664</name><description>Exported from navalgrid.com</description><Placemark><name>BF4664</name><description/><styleUrl>#default</styleUrl><Point><coordinates>-7.41666,46.95</coordinates></Point></Placemark><Placemark><name/><description/><styleUrl>#default</styleUrl><LineString><tessellate>1</tessellate><coordinates>-7.5,47 -7.33333,47 -7.33333,46.9 -7.5,46.9 -7.5,47</coordinates></LineString></Placemark></Document>"
         (sut/square->kml {:id     "BF4664"
                           :nw     [47 -7.5]
                           :se     [46.9 -7.33333]
                           :center [46.95 -7.41666]}))))