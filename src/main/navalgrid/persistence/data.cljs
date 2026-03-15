(ns navalgrid.persistence.data)

(def large-regular-squares
  [{:ids ["ÄJ" "ÄH" "ÄG" "ÄF"] :nw [85.2 -76] :se [77.1 -35.5]}])

(def large-partial-squares
  [{:ids ["OF" "OT"] :nw [41.9 167] :se [33.8 170.6] :sub [[1] [4] [7]] :o :v}])

(def irregular-squares
  [{:ids ["ÄA"] :nw [60.9 -71.5] :se [59.1 -44.5] :o :v}
   {:ids ["ÄE"] :nw [77.1 -85] :se [69 -61] :o :v}
   {:ids ["AD9"] :nw [60.9 -44.5] :se [59.1 -37.3] :o :v}])

(def polygonal-squares
  [{:id "AD" :poly [[69 -37.75] [69 -24.25] [60.9 -24.25] [60.9 -37.3] [59.1 -37.3] [59.1 -44.5] [66.3 -44.5] [66.3 -37.75]]}
   {:id "AM6" :poly [[56.4 -7] [56.4 -4] [55.5 -4] [55.5 -2.5] [53.7 -2.5] [53.7 -7]]}])

(def two-by-five-squares
  [{:ids ["AK1" "AK2" "AK3" "AL1" "AL2" "AL3" "AM1" "AM2" "AM3" "AN1"] :nw [60.9 -37.3] :se [56.4 -33.7] :so :v}])

(def partial-squares
  [{:ids ["AD1" "AD2"] :nw [69 -37.75] :se [66.3 -31] :o :h}
   {:ids ["AD3" "AD4" "AD5"] :nw [66.3 -44.5] :se [63.6 -37.75] :o :h}
   {:ids ["AD6" "AD7" "AD8"] :nw [63.6 -44.5] :se [60.9 -37.75] :o :h}
   {:ids ["AD91" "AD92" "AD93" "AD94"] :nw [60.9 -44.5] :se [60 -42.7] :o :h}
   {:ids ["AD95" "AD96" "AD97" "AD98"] :nw [60 -44.5] :se [59.1 -42.7] :o :h}
   {:ids ["AL4" "AL5"] :nw [56.4 -26.5] :se [53.7 -23.5] :sub [[1 2] [4 5] [7 8]] :o :h}])


